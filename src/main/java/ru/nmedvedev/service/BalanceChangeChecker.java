package ru.nmedvedev.service;

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
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

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
        userRepository.findSubscribedWithCard()
                .flatMap(userDb -> sodexoClient.getByCard(userDb.getCard())
                        .map(sr -> Map.entry(userDb, sr))
                        .toMulti())
                .filter(not(tuple -> tuple.getValue().getData().getHistory().isEmpty()))
                .filter(not(tuple -> equalsHistories(tuple.getKey().getLatestOperation(), tuple.getValue().getData().getHistory().get(0))))
                .map(this::updateUser)
                .invokeUni(tuple -> userRepository.persistOrUpdate(tuple.getKey()))
                .subscribe().with(tuple -> telegramService.sendMessage(tuple.getKey().getChatId(), tuple.getValue()));
    }

    private Map.Entry<UserDb, Response> updateUser(Map.Entry<UserDb, SodexoResponse> tuple) {
        var user = tuple.getKey();
        var sodexoResponse = tuple.getValue();
        var history = sodexoResponse.getData().getHistory();
        var latest = history.get(0);
        log.info("Balance changed for user {}", user.getChatId());
        //changed
        var newOperations = history.stream()
                .takeWhile(not(e -> equalsHistories(user.getLatestOperation(), e)))
                .collect(toList());
        Collections.reverse(newOperations);
        var messageText = newOperations
                .stream()
                .map(h -> String.format(
                        "%s %.2f руб от %s", h.getAmount() > 0 ? "Зачисление" : "Списание", Math.abs(h.getAmount()), h.getLocationName().get(0)
                ))
                .collect(Collectors.joining("\n", "", String.format("\nТекущий баланс %.2f руб", sodexoResponse.getData().getBalance().getAvailableAmount())));

        user.setLatestOperation(new HistoryDb(latest.getAmount(), latest.getCurrency(), latest.getLocationName().get(0), latest.getTime()));
        return Map.entry(user, Response.withReplyButtons(messageText, replyButtonsProvider.provideMenuButtons()));
    }

    private Boolean equalsHistories(HistoryDb historyDb, History history) {
        return !Objects.isNull(historyDb) && Objects.equals(historyDb.getAmount(), history.getAmount())
                && Objects.equals(historyDb.getCurrency(), history.getCurrency())
                && Objects.equals(historyDb.getLocationName(), history.getLocationName().get(0))
                && Objects.equals(historyDb.getTime(), history.getTime());
    }
}
