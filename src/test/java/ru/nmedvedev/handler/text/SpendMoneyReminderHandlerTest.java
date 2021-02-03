package ru.nmedvedev.handler.text;

import com.pengrad.telegrambot.model.User;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nmedvedev.model.UserDb;
import ru.nmedvedev.repository.UserRepository;
import ru.nmedvedev.view.ReplyButtonsProvider;
import ru.nmedvedev.view.Response;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.mockito.internal.matchers.Not;

import static ru.nmedvedev.Helper.CARD;
import static ru.nmedvedev.Helper.CHAT;
import static ru.nmedvedev.handler.text.SpendMoneyReminderHandler.SUBSCRIBE_MESSAGE;

@ExtendWith(MockitoExtension.class)
class SpendMoneyReminderHandlerTest {

    public static final long CHAT_ID = new Random().nextLong();

    @InjectMocks
    private SpendMoneyReminderHandler spendMoneyReminderHandler;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReplyButtonsProvider replyButtonsProvider;

    @BeforeEach
    void setUp() {
        when(replyButtonsProvider.provideMenuButtons()).thenReturn(List.of("1", "2"));
        when(userRepository.persistOrUpdate((UserDb) any()))
                .thenReturn(Uni.createFrom().voidItem());
    }

    @Test
    void shouldBeAbleToSubscribe() {
        when(userRepository.findByChatId(CHAT_ID))
                .thenReturn(Uni.createFrom().item(UserDb.builder().subscribedToSpendMoneyReminder(false).build()));

        var actual = spendMoneyReminderHandler.handle(CHAT_ID, "some bullshit").await().indefinitely();

        verify(userRepository, times(1))
                .persistOrUpdate(argThat((ArgumentMatcher<UserDb>) UserDb::isSubscribedToSpendMoneyReminder));

        assertEquals(Response.withReplyButtons("Теперь вам будут приходить напоминания о том, что необходимо потратить баланс",
                List.of("1", "2")), actual);
    }

    @Test
    void shouldBeAbleToUnsubscibe() {
        when(userRepository.findByChatId(CHAT_ID))
                .thenReturn(Uni.createFrom().item(UserDb.builder().subscribedToSpendMoneyReminder(true).build()));

        var actual = spendMoneyReminderHandler.handle(CHAT_ID, "some bullshit").await().indefinitely();

        verify(userRepository, times(1))
                .persistOrUpdate(argThat(((UserDb user) -> !user.isSubscribedToSpendMoneyReminder())));

        assertEquals(Response.withReplyButtons("Напоминания потратить баланс отключены", List.of("1", "2")), actual);
    }
}