package ru.nmedvedev.service;

import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nmedvedev.model.*;
import ru.nmedvedev.repository.UserRepository;
import ru.nmedvedev.rest.Constants;
import ru.nmedvedev.rest.SodexoClient;
import ru.nmedvedev.view.ReplyButtonsProvider;
import ru.nmedvedev.view.Response;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static ru.nmedvedev.Helper.CARD;
import static ru.nmedvedev.Helper.CHAT;

@ExtendWith(MockitoExtension.class)
// TODO: 19/08/2020 Refactor response creation
public class BalanceChangeCheckerTest {

    @InjectMocks
    private BalanceChangeChecker checker;

    @Mock
    private SodexoClient sodexoClient;
    @Mock
    private UserRepository userRepository;
    @Mock
    private TelegramService telegramService;
    @Mock
    private ReplyButtonsProvider replyButtonsProvider;

    private Consumer stubConsumer = (e) -> {
    };


    @Test
    void shouldCheckBalanceChangeOnlyForSubscribedUsers() {
        var chat1 = CHAT + 1;
        var chat2 = CHAT - 1;

        var card1 = CARD + "1";
        var card2 = CARD + "2";
        when(userRepository.findSubscribedWithCard())
                .thenReturn(Multi.createFrom().items(
                        UserDb.builder().chatId(chat1).card(card1).build(),
                        UserDb.builder().chatId(chat2).card(card2).build()
                ));

        var sodexoResponse = new SodexoResponse();
        sodexoResponse.setStatus(Constants.OK_STATUS);
        var data = new SodexoData();
        data.setHistory(List.of());
        data.setBalance(new Balance());
        sodexoResponse.setData(data);

        when(sodexoClient.getByCard(anyString()))
                .thenReturn(Uni.createFrom().item(sodexoResponse));

        checker.check().subscribe().with(stubConsumer);

        verify(sodexoClient, times(1)).getByCard(card1);
        verify(sodexoClient, times(1)).getByCard(card2);
    }

    @MethodSource
    @ParameterizedTest
    void shouldUpdatePreviousLatestOperation(HistoryDb latestOperation) {
        var sodexoResponse = new SodexoResponse();
        sodexoResponse.setStatus(Constants.OK_STATUS);
        var data = new SodexoData();
        data.setHistory(List.of(
                History.builder().amount(200d).currency("RUR").locationName(List.of("123")).time("2").build(),
                History.builder().amount(100d).currency("RUR").locationName(List.of("456")).time("1").build()
        ));
        data.setBalance(new Balance());
        sodexoResponse.setData(data);

        when(sodexoClient.getByCard(CARD))
                .thenReturn(Uni.createFrom().item(sodexoResponse));

        when(userRepository.findSubscribedWithCard())
                .thenReturn(Multi.createFrom().items(
                        UserDb.builder().chatId(CHAT).card(CARD).latestOperation(latestOperation).build()
                ));
        when(userRepository.persistOrUpdate((UserDb) any()))
                .thenReturn(Uni.createFrom().voidItem());

        checker.check().subscribe().with(stubConsumer);

        verify(userRepository, times(1))
                .persistOrUpdate(argThat((ArgumentMatcher<UserDb>) userDb -> userDb.getLatestOperation().equals(
                        new HistoryDb(200d, "RUR", "123", "2")
                )));
    }

    @Test
    void shouldSendNothingIfHistoryIsEmpty() {
        var sodexoResponse = new SodexoResponse();
        sodexoResponse.setStatus(Constants.OK_STATUS);
        var data = new SodexoData();
        data.setHistory(List.of());
        sodexoResponse.setData(data);

        when(sodexoClient.getByCard(CARD))
                .thenReturn(Uni.createFrom().item(sodexoResponse));

        when(userRepository.findSubscribedWithCard())
                .thenReturn(Multi.createFrom().items(
                        UserDb.builder().chatId(CHAT).card(CARD).build()
                ));

        checker.check().subscribe().with(stubConsumer);

        verify(userRepository, never()).persistOrUpdate((UserDb) any());
        verifyNoInteractions(telegramService);
    }

    @MethodSource
    @ParameterizedTest
    void shouldNotifyWithCurrentBalanceAndMenuButtons(Double amount, String operationMessage) {
        var sodexoResponse = new SodexoResponse();
        sodexoResponse.setStatus(Constants.OK_STATUS);
        var data = new SodexoData();
        data.setHistory(List.of(History.builder().amount(amount).currency("RUR").locationName(List.of("name")).build()));
        data.setBalance(new Balance(123.45, "RUR"));
        sodexoResponse.setData(data);
        when(sodexoClient.getByCard(CARD)).thenReturn(Uni.createFrom().item(sodexoResponse));

        when(replyButtonsProvider.provideMenuButtons()).thenReturn(List.of("1", "2"));

        when(userRepository.findSubscribedWithCard())
                .thenReturn(Multi.createFrom().items(
                        UserDb.builder().chatId(CHAT).card(CARD).build()
                ));
        when(userRepository.persistOrUpdate((UserDb) any()))
                .thenReturn(Uni.createFrom().voidItem());

        checker.check().subscribe().with(stubConsumer);

        verify(replyButtonsProvider, times(1)).provideMenuButtons();
        verify(telegramService, times(1))
                .sendMessage(CHAT, Response.withReplyButtons(operationMessage + "\nТекущий баланс 123.45 руб", replyButtonsProvider.provideMenuButtons()));
    }

    @Test
    void shouldSendOneMessageWithManyOperationsAndSaveLatestIfLatestOperationIsNotPresent() {
        var sodexoResponse = new SodexoResponse();
        sodexoResponse.setStatus(Constants.OK_STATUS);
        var data = new SodexoData();
        data.setHistory(List.of(
                History.builder().amount(100d).currency("RUR").locationName(List.of("name1")).time("zzz").build(),
                History.builder().amount(-200d).currency("RUR").locationName(List.of("name2")).build()
        ));
        data.setBalance(new Balance(123.45, "RUR"));
        sodexoResponse.setData(data);
        when(sodexoClient.getByCard(CARD)).thenReturn(Uni.createFrom().item(sodexoResponse));

        when(replyButtonsProvider.provideMenuButtons()).thenReturn(List.of("1", "2"));

        when(userRepository.findSubscribedWithCard())
                .thenReturn(Multi.createFrom().items(
                        UserDb.builder().chatId(CHAT).card(CARD).build()
                ));
        when(userRepository.persistOrUpdate((UserDb) any()))
                .thenReturn(Uni.createFrom().voidItem());

        checker.check().subscribe().with(stubConsumer);

        verify(replyButtonsProvider, times(1)).provideMenuButtons();
        verify(telegramService, times(1))
                .sendMessage(CHAT, Response.withReplyButtons("Списание 200.00 руб от name2\nЗачисление 100.00 руб от name1\nТекущий баланс 123.45 руб", replyButtonsProvider.provideMenuButtons()));
        verify(userRepository, times(1)).persistOrUpdate(UserDb
                .builder()
                .chatId(CHAT)
                .card(CARD)
                .latestOperation(new HistoryDb(100d, "RUR", "name1", "zzz"))
                .build());
    }

    @Test
    void shouldSendOneMessageWithManyOperationsAndSaveLatestIfLatestOperationIsPresent() {
        var sodexoResponse = new SodexoResponse();
        sodexoResponse.setStatus(Constants.OK_STATUS);
        var data = new SodexoData();
        data.setHistory(List.of(
                History.builder().amount(100d).currency("RUR").locationName(List.of("name1")).time("zzz").build(),
                History.builder().amount(-200d).currency("RUR").locationName(List.of("name2")).build(),
                History.builder().amount(-300d).currency("RUR").locationName(List.of("LOC")).time("TIME").build()
        ));
        data.setBalance(new Balance(123.45, "RUR"));
        sodexoResponse.setData(data);
        when(sodexoClient.getByCard(CARD)).thenReturn(Uni.createFrom().item(sodexoResponse));

        when(replyButtonsProvider.provideMenuButtons()).thenReturn(List.of("1", "2"));

        when(userRepository.findSubscribedWithCard())
                .thenReturn(Multi.createFrom().items(
                        UserDb.builder().chatId(CHAT).card(CARD).latestOperation(new HistoryDb(-300d, "RUR", "LOC", "TIME")).build()
                ));
        when(userRepository.persistOrUpdate((UserDb) any()))
                .thenReturn(Uni.createFrom().voidItem());

        checker.check().subscribe().with(stubConsumer);

        verify(replyButtonsProvider, times(1)).provideMenuButtons();
        verify(telegramService, times(1))
                .sendMessage(CHAT, Response.withReplyButtons("Списание 200.00 руб от name2\nЗачисление 100.00 руб от name1\nТекущий баланс 123.45 руб", replyButtonsProvider.provideMenuButtons()));
        verify(userRepository, times(1)).persistOrUpdate(UserDb
                .builder()
                .chatId(CHAT)
                .card(CARD)
                .latestOperation(new HistoryDb(100d, "RUR", "name1", "zzz"))
                .build());
    }

    @ParameterizedTest
    @ValueSource(strings = {Constants.CARD_IS_NOT_ACTIVE_STATUS, "blah-blah"})
    void shouldIgnoreUsersWithNonOkStatusResponse(String status) {
        var okChat = CHAT + 1;
        var nonOkChat = CHAT - 1;

        var okCard = CARD + "1";
        var nonOkCard = CARD + "2";
        when(userRepository.findSubscribedWithCard())
                .thenReturn(Multi.createFrom().items(
                        UserDb.builder().chatId(okChat).card(okCard).build(),
                        UserDb.builder().chatId(nonOkChat).card(nonOkCard).build()
                ));

        var okResponse = new SodexoResponse();
        okResponse.setStatus(Constants.OK_STATUS);
        var data = new SodexoData();
        data.setHistory(List.of(History.builder().amount(1d).currency("RUR").locationName(List.of("name")).build()));
        data.setBalance(new Balance(123.45, "RUR"));
        okResponse.setData(data);

        var nonOkResponse = new SodexoResponse();
        nonOkResponse.setStatus(status);

        when(sodexoClient.getByCard(okCard))
                .thenReturn(Uni.createFrom().item(okResponse));
        when(sodexoClient.getByCard(nonOkCard))
                .thenReturn(Uni.createFrom().item(nonOkResponse));

        when(userRepository.persistOrUpdate((UserDb) any()))
                .thenReturn(Uni.createFrom().voidItem());

        // may be removed
        when(replyButtonsProvider.provideMenuButtons()).thenReturn(List.of("1", "2"));

        checker.check().subscribe().with(stubConsumer);

        verify(replyButtonsProvider, times(1)).provideMenuButtons();
        verify(telegramService, times(1)).sendMessage(eq(okChat), any());
    }

    // Providers
    private static Stream<HistoryDb> shouldUpdatePreviousLatestOperation() {
        return Stream.of(
                null,
                HistoryDb
                        .builder()
                        .amount(100d)
                        .currency("RUR")
                        .locationName("456")
                        .time("1")
                        .build()
        );
    }

    private static Stream<Arguments> shouldNotifyWithCurrentBalanceAndMenuButtons() {
        return Stream.of(
                Arguments.of(123.45d, "Зачисление 123.45 руб от name"),
                Arguments.of(-123.45d, "Списание 123.45 руб от name")
        );
    }
}

