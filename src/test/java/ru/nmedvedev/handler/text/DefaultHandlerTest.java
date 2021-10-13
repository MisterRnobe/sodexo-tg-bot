package ru.nmedvedev.handler.text;

import io.smallrye.mutiny.Uni;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nmedvedev.model.SodexoResponse;
import ru.nmedvedev.model.UserDb;
import ru.nmedvedev.repository.UserRepository;
import ru.nmedvedev.rest.Constants;
import ru.nmedvedev.rest.SodexoClient;
import ru.nmedvedev.view.ReplyButtonsProvider;
import ru.nmedvedev.view.Response;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static ru.nmedvedev.Helper.CARD;
import static ru.nmedvedev.Helper.CHAT;

@ExtendWith(MockitoExtension.class)
class DefaultHandlerTest {

    @InjectMocks
    private DefaultHandler defaultHandler;

    @Mock
    private UserRepository userRepository;
    @Mock
    private SodexoClient sodexoClient;
    @Mock
    private ReplyButtonsProvider replyButtonsProvider;

    @Test
    void shouldReturnAcceptMessageWithMenuButtonsAndPersistIfCardIsValid() {
        var sodexoResponse = new SodexoResponse();
        sodexoResponse.setStatus("OK");

        when(sodexoClient.getByCard(CARD))
                .thenReturn(Uni.createFrom().item(sodexoResponse));
        when(userRepository.findByChatId(CHAT))
                .thenReturn(Uni.createFrom().item(() -> null));
        when(replyButtonsProvider.provideMenuButtons())
                .thenReturn(List.of("1", "2"));
        when(userRepository.persistOrUpdate((UserDb) any()))
                .thenReturn(Uni.createFrom().item(() -> null));

        var actual = defaultHandler.handle(CHAT, CARD).await().indefinitely();

        verify(userRepository, times(1)).persistOrUpdate(UserDb.builder().chatId(CHAT).card(CARD).build());
        verify(replyButtonsProvider, times(1)).provideMenuButtons();
        assertEquals(Response.withReplyButtons("Я сохранил карту " + CARD, replyButtonsProvider.provideMenuButtons()), actual);
        verify(sodexoClient, times(1)).getByCard(CARD);
    }

    @Test
    void shouldReturnAcceptMessageWithMenuButtonsAndUpdateUserIfCardIsValid() {
        var user = UserDb.builder().chatId(CHAT).id(new ObjectId()).build();

        var sodexoResponse = new SodexoResponse();
        sodexoResponse.setStatus("OK");

        when(sodexoClient.getByCard(CARD))
                .thenReturn(Uni.createFrom().item(sodexoResponse));
        when(userRepository.findByChatId(CHAT))
                .thenReturn(Uni.createFrom().item(user));
        when(replyButtonsProvider.provideMenuButtons())
                .thenReturn(List.of("1", "2"));
        when(userRepository.persistOrUpdate((UserDb) any()))
                .thenReturn(Uni.createFrom().item(() -> null));

        var actual = defaultHandler.handle(CHAT, CARD).await().indefinitely();

        verify(sodexoClient, times(1)).getByCard(CARD);
        var expectedToSave = UserDb.builder().id(user.getId()).chatId(CHAT).card(CARD).build();
        verify(userRepository, times(1)).persistOrUpdate(expectedToSave);
        verify(replyButtonsProvider, times(1)).provideMenuButtons();
        assertEquals(Response.withReplyButtons("Я сохранил карту " + CARD, replyButtonsProvider.provideMenuButtons()), actual);
    }

    @Test
    void shouldReturnErrorTextIfSodexoClientReturnedError() {
        var sodexoResponse = new SodexoResponse();
        sodexoResponse.setStatus("ERROR");

        when(userRepository.findByChatId(CHAT))
                .thenReturn(Uni.createFrom().item(() -> null));
        when(sodexoClient.getByCard(CARD))
                .thenReturn(Uni.createFrom().item(sodexoResponse));

        var actual = defaultHandler.handle(CHAT, CARD).await().indefinitely();

        assertEquals(Response.fromText("Карты " + CARD + " не существует, повторите ввод"), actual);
        verify(sodexoClient, times(1)).getByCard(CARD);
        verify(userRepository, never()).persist((UserDb) any());
        verifyNoInteractions(replyButtonsProvider);
    }

    @Test
    void shouldReturnCardInvalidIfSodexoClientReturnedCardInactive() {
        var sodexoResponse = new SodexoResponse();
        sodexoResponse.setStatus(Constants.CARD_IS_NOT_ACTIVE_STATUS);

        when(userRepository.findByChatId(CHAT))
                .thenReturn(Uni.createFrom().nullItem());
        when(sodexoClient.getByCard(CARD))
                .thenReturn(Uni.createFrom().item(sodexoResponse));

        var actual = defaultHandler.handle(CHAT, CARD).await().indefinitely();

        assertEquals(Response.fromText("Карта" + CARD + " устарела или не активна по другим причинам, повторите ввод"), actual);
        verify(sodexoClient, times(1)).getByCard(CARD);
        verify(userRepository, never()).persist((UserDb) any());
        verifyNoInteractions(replyButtonsProvider);
    }

    @Test
    void shouldReturnErrorMessageWithButtonsForBadMessageIfCardIsPresent() {
        when(userRepository.findByChatId(CHAT))
                .thenReturn(Uni.createFrom().item(UserDb.builder().card(CARD).build()));
        when(replyButtonsProvider.provideMenuButtons())
                .thenReturn(List.of("1", "2"));

        var actual = defaultHandler.handle(CHAT, "ZZZZZZZ").await().indefinitely();

        verify(replyButtonsProvider, times(1)).provideMenuButtons();
        verify(userRepository, never()).persistOrUpdate((UserDb) any());
        verifyNoInteractions(sodexoClient);
        assertEquals(Response.withReplyButtons("Неизвестный запрос :(", replyButtonsProvider.provideMenuButtons()), actual);
    }

    @Test
    void shouldReturnInvalidCardTextWithNoButtonsForBadMessageIfCardIsNotPresent() {
        when(userRepository.findByChatId(CHAT))
                .thenReturn(Uni.createFrom().item(new UserDb()));

        var actual = defaultHandler.handle(CHAT, "ZZZZZZZ").await().indefinitely();

        assertEquals(Response.fromText("Неверный формат карты"), actual);
        verify(userRepository, never()).persistOrUpdate((UserDb) any());
        verifyNoInteractions(sodexoClient, replyButtonsProvider);
    }

    @Test
    void shouldReturnErrorTextWithNoButtonsForBadMessageIfUserIsNotPresent() {
        when(userRepository.findByChatId(CHAT))
                .thenReturn(Uni.createFrom().item(() -> null));

        var actual = defaultHandler.handle(CHAT, "ZZZZZZZ").await().indefinitely();

        assertEquals(Response.fromText("Неверный формат карты"), actual);
        verify(userRepository, never()).persistOrUpdate((UserDb) any());
        verifyNoInteractions(sodexoClient, replyButtonsProvider);
    }

    @ParameterizedTest
    @ValueSource(strings = {"1 2 3", "123", " 123", "123  "})
    void shouldRemoveSpaces(String card) {
        var trimmed = "123";
        var sodexoResponse = new SodexoResponse();
        sodexoResponse.setStatus("OK");

        when(sodexoClient.getByCard(trimmed))
                .thenReturn(Uni.createFrom().item(sodexoResponse));
        when(userRepository.findByChatId(CHAT))
                .thenReturn(Uni.createFrom().item(() -> null));
        when(replyButtonsProvider.provideMenuButtons())
                .thenReturn(List.of("1", "2"));
        when(userRepository.persistOrUpdate((UserDb) any()))
                .thenReturn(Uni.createFrom().voidItem());

        var actual = defaultHandler.handle(CHAT, card).await().indefinitely();

        assertEquals(Response.withReplyButtons("Я сохранил карту 123", replyButtonsProvider.provideMenuButtons()), actual);
        verify(sodexoClient, times(1)).getByCard(trimmed);
        verify(userRepository, times(1)).persistOrUpdate(UserDb.builder().chatId(CHAT).card(trimmed).build());
    }

    @Test
    void shouldReturnInternalErrorTextIfRestClientFailed() {

    }


}
