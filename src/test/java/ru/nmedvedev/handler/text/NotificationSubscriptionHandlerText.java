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
import ru.nmedvedev.model.History;
import ru.nmedvedev.model.HistoryDb;
import ru.nmedvedev.model.SodexoData;
import ru.nmedvedev.model.SodexoResponse;
import ru.nmedvedev.model.UserDb;
import ru.nmedvedev.repository.UserRepository;
import ru.nmedvedev.rest.SodexoClient;
import ru.nmedvedev.view.ReplyButtonsProvider;
import ru.nmedvedev.view.Response;

import java.util.List;
import java.util.function.Supplier;
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
    @Mock
    private SodexoClient sodexoClient;

    @Test
    void shouldSaveEnabledNotificationWithLatestOperationAndReturnTextWithMenuButtons() {
        when(sodexoClient.getByCard(CARD))
                .thenReturn(Uni.createFrom().item(sodexoResponse()));
        when(userRepository.findByChatId(CHAT))
                .thenReturn(Uni.createFrom().item(UserDb.builder().card(CARD).subscribed(false).build()));
        when(userRepository.persistOrUpdate((UserDb) any()))
                .thenReturn(Uni.createFrom().item(() -> null));
        when(replyButtonsProvider.provideMenuButtons())
                .thenReturn(List.of("1", "2", "3"));

        var actual = handler.handle(CHAT, "").await().indefinitely();

        var historyDb = HistoryDb.builder()
                .amount(100d)
                .currency("currency")
                .locationName("location")
                .time("time")
                .build();
        verify(userRepository, times(1)).persistOrUpdate(UserDb.builder().card(CARD).subscribed(true).latestOperation(historyDb).build());
        verify(replyButtonsProvider, times(1)).provideMenuButtons();
        assertEquals(Response.withReplyButtons("Теперь я вам буду сообщать о всех зачислениям и списаниях", replyButtonsProvider.provideMenuButtons()), actual);
    }

    @Test
    void shouldSaveDisabledNotificationAndReturnTextWithMenuButtons() {
        var id = new ObjectId();
        when(userRepository.findByChatId(CHAT))
                .thenReturn(Uni.createFrom().item(UserDb.builder().subscribed(true).build()));
        when(userRepository.persistOrUpdate((UserDb) any()))
                .thenReturn(Uni.createFrom().item(() -> null));
        when(replyButtonsProvider.provideMenuButtons())
                .thenReturn(List.of("1", "2", "3"));

        var actual = handler.handle(CHAT, "").await().indefinitely();

        verify(userRepository, times(1)).persistOrUpdate(UserDb.builder().subscribed(false).build());
        verify(replyButtonsProvider, times(1)).provideMenuButtons();
        assertEquals(Response.withReplyButtons("Теперь я вам не буду сообщать о всех зачислениям и списаниях", replyButtonsProvider.provideMenuButtons()), actual);
        verifyNoInteractions(sodexoClient);
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

    private SodexoResponse sodexoResponse() {
        var sodexoData = new SodexoData();
        sodexoData.setHistory(List.of(
                new History(100d, "currency", List.of("location"), "mcc", "merchantId", "time", 10)
        ));
        return new SodexoResponse("OK", sodexoData);
    }
}
