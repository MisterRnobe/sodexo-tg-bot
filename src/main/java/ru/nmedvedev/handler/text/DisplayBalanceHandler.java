package ru.nmedvedev.handler.text;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import ru.nmedvedev.handler.InputTextHandler;
import ru.nmedvedev.model.UserDb;
import ru.nmedvedev.repository.UserRepository;
import ru.nmedvedev.rest.SodexoClient;
import ru.nmedvedev.view.ReplyButtonsProvider;
import ru.nmedvedev.view.Response;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class DisplayBalanceHandler implements InputTextHandler {

    private final UserRepository userRepository;
    private final SodexoClient sodexoClient;
    private final ReplyButtonsProvider replyButtonsProvider;

    public DisplayBalanceHandler(UserRepository userRepository,
                                 @RestClient SodexoClient sodexoClient,
                                 ReplyButtonsProvider replyButtonsProvider) {
        this.userRepository = userRepository;
        this.sodexoClient = sodexoClient;
        this.replyButtonsProvider = replyButtonsProvider;
    }

    @Override
    public String getPattern() {
        return "Покажи баланс";
    }

    @Override
    public Uni<Response> handle(Long chatId, String text) {
        return userRepository.findByChatId(chatId)
                // TODO: 15/08/2020 WTF, how to get rid of .onItem().ifNotNull()????
                .onItem().ifNotNull().transform(UserDb::getCard)
                .onItem().ifNotNull().transformToUni(sodexoClient::getByCard)
                .onItem().ifNotNull().transform(response -> String.format(
                        "Ваш баланс %.2f %s",
                        response.getData().getBalance().getAvailableAmount(),
                        "руб"))
                .onItem().ifNotNull().transform(str -> Response.withReplyButtons(str, replyButtonsProvider.provideMenuButtons()))
                .onItem().ifNull().continueWith(() -> Response.fromText("Вы не ввели карту"));
    }
}
