package ru.nmedvedev.handler.text;

import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nmedvedev.model.UserDb;
import ru.nmedvedev.repository.UserRepository;
import ru.nmedvedev.view.ReplyButtonsProvider;
import ru.nmedvedev.view.Response;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static ru.nmedvedev.Helper.CARD;
import static ru.nmedvedev.Helper.CHAT;

@ExtendWith(MockitoExtension.class)
class CardDisplayHandlerTest {

    @InjectMocks
    private CardDisplayHandler handler;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ReplyButtonsProvider replyButtonsProvider;

    @Test
    void shouldReturnCardWithButtonsIfCardIsPresent() {
        var buttons = List.of("Elem1", "elem2");

        when(userRepository.findByChatId(CHAT))
                .thenReturn(Uni.createFrom().item(UserDb.builder().card(CARD).build()));
        when(replyButtonsProvider.provideMenuButtons())
                .thenReturn(buttons);

        var actual = handler.handle(CHAT, "").await().indefinitely();

        var expected = Response.withReplyButtons("Ваша карта " + CARD, buttons);
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnErrorMessageWithNoButtonsIfCardIsNotPresent() {
        when(userRepository.findByChatId(CHAT))
                .thenReturn(Uni.createFrom().item(new UserDb()));

        var actual = handler.handle(CHAT, "").await().indefinitely();

        var expected = Response.fromText("Вы не ввели карту");
        assertEquals(expected, actual);
        verifyNoInteractions(replyButtonsProvider);
    }

    @Test
    void shouldReturnErrorMessageIfUserIsNotPresent() {
        when(userRepository.findByChatId(CHAT))
                .thenReturn(Uni.createFrom().item(() -> null));

        var actual = handler.handle(CHAT, "").await().indefinitely();

        var expected = Response.fromText("Вы не ввели карту");
        assertEquals(expected, actual);
        verifyNoInteractions(replyButtonsProvider);
    }
}
