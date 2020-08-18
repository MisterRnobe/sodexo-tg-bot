package ru.nmedvedev.service;

import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import ru.nmedvedev.model.History;
import ru.nmedvedev.model.HistoryDb;
import ru.nmedvedev.model.SodexoResponse;
import ru.nmedvedev.model.UserDb;
import ru.nmedvedev.repository.UserRepository;
import ru.nmedvedev.rest.SodexoClient;
import ru.nmedvedev.view.ReplyButtonsProvider;
import ru.nmedvedev.view.Response;

import javax.enterprise.context.ApplicationScoped;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;

@Slf4j
@ApplicationScoped
public class BalanceChangeChecker {

    private final SodexoClient sodexoClient;
    private final UserRepository userRepository;
    private final TelegramService telegramService;
    private final ReplyButtonsProvider replyButtonsProvider;

    public BalanceChangeChecker(@RestClient SodexoClient sodexoClient,
                                UserRepository userRepository,
                                TelegramService telegramService,
                                ReplyButtonsProvider replyButtonsProvider) {
        this.sodexoClient = sodexoClient;
        this.userRepository = userRepository;
        this.telegramService = telegramService;
        this.replyButtonsProvider = replyButtonsProvider;
    }

    public void check() {
        // TODO: 16/08/2020 Definitely it's not a good implementation
        var users = userRepository.findSubscribedWithCard()
                .collectItems().asList().await().indefinitely();

        for (var user : users) {
            var sodexoResponse = sodexoClient.getByCard(user.getCard()).await().indefinitely();
            var history = sodexoResponse.getData().getHistory();
            if (!history.isEmpty()) {
                var latest = history.get(0);
                if (!equalsHistories(user.getLatestOperation(), latest)) {
                    log.info("Balance changed for user {}", user.getChatId());
                    //changed
                    var copy = new ArrayList<>(history);
                    Collections.reverse(copy);
                    var messageText = copy.stream()
                            .takeWhile(not(e -> equalsHistories(user.getLatestOperation(), e)))
                            .map(h -> String.format(
                                    "%s %.2f руб от %s", h.getAmount() > 0 ? "Зачисление" : "Списание", Math.abs(h.getAmount()), h.getLocationName().get(0)
                            ))
                            .collect(Collectors.joining("\n","", String.format("\nТекущий баланс %.2f руб", sodexoResponse.getData().getBalance().getAvailableAmount())));

                    user.setLatestOperation(new HistoryDb(latest.getAmount(), latest.getCurrency(), latest.getLocationName().get(0), latest.getTime()));
                    userRepository.persistOrUpdate(user).await().indefinitely();
                    telegramService.sendMessage(user.getChatId(), Response.withKeyboardButton(messageText, replyButtonsProvider.provideMenuButtons())).await().indefinitely();
                }
            }
        }


//        userRepository.findSubscribedWithCard()
//                .flatMap(userDb -> sodexoClient.getByCard(userDb.getCard()).map(resp -> Map.entry(userDb, resp)).toMulti())
//                .filter(balanceChanged())
//                .invokeUni(saveToDb())
//                .map(convertToResponses())
//                .flatMap(tuple -> Multi.createFrom().items(tuple.getValue().stream()
//                        .map(r -> Map.entry(tuple.getKey(), r))
//                ))
//                .invokeUni(sendMessage())
//                .subscribe().with(r -> log.info("Sent message for " + r));

    }

    private Function<? super Map.Entry<Long, Response>, ? extends Uni<?>> sendMessage() {
        return tuple -> telegramService.sendMessage(tuple.getKey(), tuple.getValue());
    }

    private Function<? super Map.Entry<UserDb, SodexoResponse>, ? extends Uni<?>> saveToDb() {
        return tuple -> {
            var userDb = tuple.getKey();
            var latest = tuple.getValue().getData().getHistory().get(0);
            var updated = UserDb.builder()
                    .id(userDb.id)
                    .card(userDb.getCard())
                    .chatId(userDb.getChatId())
                    .subscribed(userDb.getSubscribed())
                    // TODO: 16/08/2020 Move to converter?
                    .latestOperation(new HistoryDb(latest.getAmount(), latest.getCurrency(), latest.getLocationName().get(0), latest.getTime()))
                    .build();
            log.info("Saving " + updated);
            return userRepository.persistOrUpdate(updated);
        };
    }

    private Function<? super Map.Entry<UserDb, SodexoResponse>, Map.Entry<Long, List<Response>>> convertToResponses() {
        return tuple -> {
            return Map.entry(tuple.getKey().getChatId(), List.of());
        };
    }

//    private Predicate<? super Map.Entry<UserDb, SodexoResponse>> balanceChanged() {
//        return tuple -> {
//            var latestOperation = tuple.getKey().getLatestOperation();
//            var sodexoResponse = tuple.getValue();
//            return Optional.of(sodexoResponse)
//                    .map(SodexoResponse::getData)
//                    .map(SodexoData::getHistory)
//                    .filter(not(List::isEmpty))
//                    .stream()
//                    .flatMap(Collection::stream)
//                    .takeWhile(not(equalsHistories(latestOperation)))
//                    .findFirst()
//                    .isPresent();
//        };
//    }

    private Boolean equalsHistories(HistoryDb historyDb, History history) {
        return !Objects.isNull(historyDb) && Objects.equals(historyDb.getAmount(), history.getAmount())
                && Objects.equals(historyDb.getCurrency(), history.getCurrency())
                && Objects.equals(historyDb.getLocationName(), history.getLocationName().get(0))
                && Objects.equals(historyDb.getTime(), history.getTime());
    }
}
