package ru.nmedvedev.service.spendmoneyreminder;

import io.smallrye.mutiny.Multi;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nmedvedev.model.HistoryDb;
import ru.nmedvedev.model.UserDb;
import ru.nmedvedev.repository.UserRepository;
import ru.nmedvedev.service.TelegramService;
import ru.nmedvedev.view.ReplyButtonsProvider;

import java.util.Random;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SpendMoneyReminderServiceTest {

    @InjectMocks
    private SpendMoneyReminderService service;

    @Mock
    private RemindDayProviderService remindDayProviderService;
    @Mock
    private SpendMoneyReminderBusinessLogicGateService spendMoneyReminderBusinessLogicGateService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TelegramService telegramService;
    @Mock
    private ReplyButtonsProvider replyButtonsProvider;

    private static final Long CHAT_ID = new Random().nextLong();

    @Test
    void doNotFireAnyReminderOnAWrongDay() {
        when(remindDayProviderService.getDay(any())).thenReturn(ReminderDayEnum.NOT_A_DAY_FOR_A_REMINDER);
        service.sendReminders();
        verifyNoInteractions(spendMoneyReminderBusinessLogicGateService);
        verifyNoInteractions(userRepository);
        verifyNoInteractions(telegramService);
        verifyNoInteractions(replyButtonsProvider);
    }

    @Test
    void doNotSendIfUserHasNoCard() {
        when(remindDayProviderService.getDay(any())).thenReturn(ReminderDayEnum.LAST_WORKING_DAY_MINUS_ONE);
        when(userRepository.findSubscribedToSpendMoneyReminderWithCardAndChat())
                .thenReturn(Multi.createFrom().items(
                        UserDb.builder().build()
                ));

        service.sendReminders();
        verifyNoInteractions(telegramService);
        verifyNoInteractions(spendMoneyReminderBusinessLogicGateService);
    }

    @Test
    void doNotSendIfUserFailedBusinessLogicGate() {
        when(remindDayProviderService.getDay(any())).thenReturn(ReminderDayEnum.LAST_WORKING_DAY_MINUS_ONE);
        when(userRepository.findSubscribedToSpendMoneyReminderWithCardAndChat())
                .thenReturn(Multi.createFrom().items(
                        UserDb.builder().latestOperation(HistoryDb.builder().amount(100.).build()).build()
                ));
        when(spendMoneyReminderBusinessLogicGateService.needToSendNotification(any(), eq(100.))).thenReturn(false);

        service.sendReminders();
        verifyNoInteractions(telegramService);
    }

    @Test
    void sendIfEverythingIsOk() {
        when(remindDayProviderService.getDay(any())).thenReturn(ReminderDayEnum.LAST_WORKING_DAY_MINUS_ONE);
        when(userRepository.findSubscribedToSpendMoneyReminderWithCardAndChat())
                .thenReturn(Multi.createFrom().items(
                        UserDb.builder()
                                .chatId(CHAT_ID)
                                .latestOperation(HistoryDb.builder().amount(100.).build())
                                .build()
                ));
        when(spendMoneyReminderBusinessLogicGateService.needToSendNotification(any(), eq(100.))).thenReturn(true);

        service.sendReminders();
        verify(telegramService, times(1)).sendMessage(eq(CHAT_ID), any());
    }
}
