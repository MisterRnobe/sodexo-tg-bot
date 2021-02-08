package ru.nmedvedev.service.spendmoneyreminder;

import io.quarkus.scheduler.Scheduled;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import ru.nmedvedev.model.UserDb;
import ru.nmedvedev.repository.UserRepository;
import ru.nmedvedev.rest.SodexoCustomClient;
import ru.nmedvedev.service.TelegramService;
import ru.nmedvedev.view.ReplyButtonsProvider;
import ru.nmedvedev.view.Response;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.time.LocalDate;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class SpendMoneyReminderService {

    private final RemindDayProviderService remindDayProviderService;
    private final SpendMoneyReminderBusinessLogicGateService spendMoneyReminderBusinessLogicGateService;
    private final UserRepository userRepository;
    private final TelegramService telegramService;
    private final ReplyButtonsProvider replyButtonsProvider;
    private final SodexoCustomClient sodexoClient;

    @Scheduled(cron = "0 0 12 * * ?")
    public void sendReminders() {
        LocalDate date = LocalDate.now();
        sendRemindersForDate(date);
    }

    public void sendRemindersForDate(LocalDate date) {
        log.info("Send spend money reminder cron triggered for date {}", date);
        ReminderDayEnum day = remindDayProviderService.getDay(date);
        if (day == ReminderDayEnum.NOT_A_DAY_FOR_A_REMINDER) {
            log.info("It is NOT a day for a reminder");
            return;
        }

        log.info("It IS a day for a reminder indeed: {}", day.name());
        userRepository.findSubscribedToSpendMoneyReminderWithCardAndChat()
                .map(user -> new ExtendedUser(user, sodexoClient.getAmount(user.getCard())
                        .await()
                        .asOptional()
                        .atMost(Duration.ofSeconds(10))
                        .orElse(null)))
                .filter(user -> user.getAmount() != null)
                .filter(user -> spendMoneyReminderBusinessLogicGateService.needToSendNotification(day, user.amount))
                .invoke(user -> telegramService.sendMessage(user.getChatId(), Response.withReplyButtons(
                        String.format(day.messageFormat, user.amount),
                        replyButtonsProvider.provideMenuButtons()
                )))
                .onFailure()
                .retry()
                .atMost(5)
                .subscribe()
                .with(user -> log.info("Sent notification for user {} to chat {} with amount {}",
                        user.getId(), user.getChatId(), user.amount));
    }

    @Value
    private static class ExtendedUser {
        UserDb userDb;
        Double amount;

        Long getChatId() {
            return userDb.getChatId();
        }

        ObjectId getId() {
            return userDb.getId();
        }
    }
}
