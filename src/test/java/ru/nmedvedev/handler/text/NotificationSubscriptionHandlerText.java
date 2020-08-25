package ru.nmedvedev.handler.text;

import io.smallrye.mutiny.Uni;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nmedvedev.model.UserDb;
import ru.nmedvedev.repository.UserRepository;
import ru.nmedvedev.view.ReplyButtonsProvider;
import ru.nmedvedev.view.Response;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static ru.nmedvedev.Helper.CARD;
import static ru.nmedvedev.Helper.CHAT;

@ExtendWith(MockitoExtension.class)
public class NotificationSubscriptionHandlerText {

    @InjectMocks
    private NotificationSubscriptionHandler handler;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ReplyButtonsProvider replyButtonsProvider;

    @MethodSource
    @ParameterizedTest
    void shouldSaveWithSwitchedStatusAndReturnTextWithMenuButtons(boolean subscribed, String message) {
        var id = new ObjectId();
        when(userRepository.findByChatId(CHAT))
                .thenReturn(Uni.createFrom().item(UserDb.withId(id, CHAT, CARD, subscribed)));
        when(userRepository.persistOrUpdate((UserDb) any()))
                .thenReturn(Uni.createFrom().item(() -> null));
        when(replyButtonsProvider.provideMenuButtons())
                .thenReturn(List.of("1", "2", "3"));

        var actual = handler.handle(CHAT, "").await().indefinitely();

        verify(userRepository, times(1)).persistOrUpdate(UserDb.withId(id, CHAT, CARD, !subscribed));
        verify(replyButtonsProvider, times(1)).provideMenuButtons();
        assertEquals(Response.withReplyButtons(message, replyButtonsProvider.provideMenuButtons()), actual);
    }

    @Test
    void shouldReturnTextErrorWithNoButtonsIfUserIsNotPresent() {
        when(userRepository.findByChatId(CHAT))
                .thenReturn(Uni.createFrom().item(() -> null));

        var actual = handler.handle(CHAT, "").await().indefinitely();

        verify(userRepository, never()).persistOrUpdate((UserDb) any());
        verifyNoInteractions(replyButtonsProvider);
        assertEquals(Response.fromText("Вы не ввели карту"), actual);
    }

    private static Stream<Arguments> shouldSaveWithSwitchedStatusAndReturnTextWithMenuButtons() {
        return Stream.of(
                Arguments.of(false, "Теперь я вам буду сообщать о всех зачислениям и списаниях"),
                Arguments.of(true, "Теперь я вам не буду сообщать о всех зачислениям и списаниях")
        );
    }
}
