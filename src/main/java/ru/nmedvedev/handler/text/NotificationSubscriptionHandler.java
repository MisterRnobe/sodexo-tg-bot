package ru.nmedvedev.handler.text;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import ru.nmedvedev.handler.InputTextHandler;
import ru.nmedvedev.model.HistoryDb;
import ru.nmedvedev.model.SodexoData;
import ru.nmedvedev.model.SodexoResponse;
import ru.nmedvedev.model.UserDb;
import ru.nmedvedev.repository.UserRepository;
import ru.nmedvedev.rest.SodexoClient;
import ru.nmedvedev.view.ReplyButtonsProvider;
import ru.nmedvedev.view.Response;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class NotificationSubscriptionHandler implements InputTextHandler {

    private final UserRepository userRepository;
    private final ReplyButtonsProvider replyButtonsProvider;
    private final SodexoClient sodexoClient;

    public NotificationSubscriptionHandler(UserRepository userRepository,
                                           ReplyButtonsProvider replyButtonsProvider,
                                           @RestClient SodexoClient sodexoClient) {
        this.userRepository = userRepository;
        this.replyButtonsProvider = replyButtonsProvider;
        this.sodexoClient = sodexoClient;
    }

    @Override
    public String getPattern() {
        return "Подпиши или отпиши на уведомления о балансе";
    }

    @Override
    public Uni<Response> handle(Long chatId, String text) {
        return userRepository.findByChatId(chatId)
                .onItem().ifNotNull().transformToUni(this::handleIfUserExists)
                .onItem().ifNull().continueWith(() -> Response.fromText("Вы не ввели карту"));
    }

    private Uni<Response> handleIfUserExists(UserDb userDb) {
        userDb.setSubscribed(!userDb.getSubscribed());
        var message = userDb.getSubscribed()
                ? "Теперь я вам буду сообщать о всех зачислениям и списаниях"
                : "Теперь я вам не буду сообщать о всех зачислениям и списаниях";
        return Uni.createFrom().item(userDb)
                .onItem().transformToUni(this::updateLatestOperation)
                .onItem().transform(userRepository::persistOrUpdate)
                .map(v -> Response.withReplyButtons(message, replyButtonsProvider.provideMenuButtons()));
    }

    private Uni<? extends UserDb> updateLatestOperation(UserDb userDb) {
        if (userDb.getSubscribed()) {
            return sodexoClient.getByCard(userDb.getCard())
                    .map(SodexoResponse::getData)
                    .map(SodexoData::getHistory)
                    .map(list -> list.get(0))
                    .map(latest -> new HistoryDb(latest.getAmount(), latest.getCurrency(), latest.getLocationName().get(0), latest.getTime()))
                    .map(historyDb -> UserDb
                            .builder()
                            .id(userDb.getId())
                            .card(userDb.getCard())
                            .chatId(userDb.getChatId())
                            .subscribed(userDb.getSubscribed())
                            .latestOperation(historyDb)
                            .build());
        } else {
            return Uni.createFrom().item(userDb);
        }
    }
}
