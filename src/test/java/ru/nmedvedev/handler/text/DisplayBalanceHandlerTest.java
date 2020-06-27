package ru.nmedvedev.handler.text;

import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nmedvedev.model.Balance;
import ru.nmedvedev.model.SodexoData;
import ru.nmedvedev.model.SodexoResponse;
import ru.nmedvedev.model.UserDb;
import ru.nmedvedev.repository.UserRepository;
import ru.nmedvedev.rest.SodexoClient;
import ru.nmedvedev.view.ReplyButtonsProvider;
import ru.nmedvedev.view.Response;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static ru.nmedvedev.Helper.CARD;
import static ru.nmedvedev.Helper.CHAT;

@ExtendWith(MockitoExtension.class)
class DisplayBalanceHandlerTest {

    @InjectMocks
    private DisplayBalanceHandler handler;
    @Mock
    private UserRepository userRepository;
    @Mock
    private SodexoClient sodexoClient;
    @Mock
    private ReplyButtonsProvider replyButtonsProvider;

    @Test
    void shouldReturnBalanceWithMenuButtonsIfCardIsPresent() {
        when(replyButtonsProvider.provideMenuButtons())
                .thenReturn(List.of("1", "2"));

        when(userRepository.findByChatId(CHAT))
                .thenReturn(Uni.createFrom().item(new UserDb(CHAT, CARD, false)));

        var sodexoData = new SodexoData();
        sodexoData.setBalance(new Balance(10d, "RUR"));
        when(sodexoClient.getByCard(CARD))
                .thenReturn(Uni.createFrom().item(new SodexoResponse("OK", sodexoData)));
        // When
        var actual = handler.handle(CHAT, "").await().indefinitely();
        // Then
        verify(replyButtonsProvider, times(1)).provideMenuButtons();
        var expected = Response.withKeyboardButton("Ваш баланс 10.00 руб", replyButtonsProvider.provideMenuButtons());
        assertEquals(expected, actual);
    }

    @Test
    void shouldReturnErrorWithNoButtonsIfCardIsNotPresent() {
        when(userRepository.findByChatId(CHAT))
                .thenReturn(Uni.createFrom().item(new UserDb(CHAT, null, false)));

        // When
        var actual = handler.handle(CHAT, "").await().indefinitely();
        // Then
        verifyNoInteractions(replyButtonsProvider, sodexoClient);
        assertEquals(Response.fromText("Вы не ввели карту"), actual);
    }

    @Test
    void shouldReturnErrorWithNoButtonsIfUserIsNotPresent() {
        when(userRepository.findByChatId(CHAT))
                .thenReturn(Uni.createFrom().item(() -> null));

        // When
        var actual = handler.handle(CHAT, "").await().indefinitely();
        // Then
        verifyNoInteractions(replyButtonsProvider, sodexoClient);
        assertEquals(Response.fromText("Вы не ввели карту"), actual);
    }

    @Test
    void shouldReturnErrorWithMenuButtonsIfSodexoClientFailed() {

    }
}
