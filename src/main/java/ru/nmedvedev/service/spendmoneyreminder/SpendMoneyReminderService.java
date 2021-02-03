package ru.nmedvedev.service.spendmoneyreminder;

import io.quarkus.scheduler.Scheduled;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.nmedvedev.model.HistoryDb;
import ru.nmedvedev.repository.UserRepository;
import ru.nmedvedev.service.TelegramService;
import ru.nmedvedev.view.ReplyButtonsProvider;
import ru.nmedvedev.view.Response;

import javax.enterprise.context.ApplicationScoped;
import java.time.LocalDate;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class SpendMoneyReminderService {

    private final RemindDayProviderService remindDayProviderService;
    private final SpendMoneyReminderBusinessLogicGateService spendMoneyReminderBusinessLogicGateService;
    private final UserRepository userRepository;
    private final TelegramService telegramService;
    private final ReplyButtonsProvider replyButtonsProvider;

    @Scheduled(cron = "0 0 12 * * ?")
    public void sendReminders() {
        log.info("Send spend money reminder cron triggered");
        ReminderDayEnum day = remindDayProviderService.getDay(LocalDate.now());
        if (day == ReminderDayEnum.NOT_A_DAY_FOR_A_REMINDER) {
            log.info("It is NOT a day for a reminder");
            return;
        }

        log.info("It IS a day for a reminder indeed: {}", day.name());
        userRepository.findSubscribedToSpendMoneyReminderWithCardAndChat()
                .filter(user -> Optional.ofNullable(user.getLatestOperation()).map(HistoryDb::getAmount)
                        .map(amountt -> spendMoneyReminderBusinessLogicGateService.needToSendNotification(day, amountt))
                        .orElse(false))
                .invoke(user -> telegramService.sendMessage(user.getChatId(), Response.withReplyButtons(
                        String.format(day.messageFormat, user.getLatestOperation().getAmount()),
                        replyButtonsProvider.provideMenuButtons()
                )))
                .onFailure()
                .retry()
                .atMost(5)
                .subscribe()
                .with(user -> log.info("Sent notification for user {} to chat {} with amount {}",
                        user.getId(), user.getChatId(), user.getLatestOperation().getAmount()));
    }
}
