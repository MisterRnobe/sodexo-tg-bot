package ru.nmedvedev.handler.text;

import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import ru.nmedvedev.handler.InputTextHandler;
import ru.nmedvedev.model.SodexoResponse;
import ru.nmedvedev.model.UserDb;
import ru.nmedvedev.repository.UserRepository;
import ru.nmedvedev.rest.SodexoClient;
import ru.nmedvedev.view.ReplyButtonsProvider;
import ru.nmedvedev.view.Response;

import javax.enterprise.context.ApplicationScoped;

import static ru.nmedvedev.rest.Constants.CARD_IS_NOT_ACTIVE_STATUS;
import static ru.nmedvedev.rest.Constants.OK_STATUS;

@Slf4j
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
                .onItem().ifNotNull().apply(UserDb::getCard)
                .onItem().ifNotNull().produceUni(sodexoClient::getByCard)
                .onItem().ifNotNull().apply(this::getResponse)
                .onItem().ifNull().continueWith(() -> Response.fromText("Вы не ввели карту"));
    }

    private Response getResponse(SodexoResponse sodexoResponse) {
        log.info("Got response {}", sodexoResponse);
        if (sodexoResponse.getStatus().equals(OK_STATUS)) {
            var text = String.format(
                    "Ваш баланс %.2f %s",
                    sodexoResponse.getData().getBalance().getAvailableAmount(),
                    "руб");
            return Response.withReplyButtons(text, replyButtonsProvider.provideMenuButtons());
        } else if (sodexoResponse.getStatus().equals(CARD_IS_NOT_ACTIVE_STATUS)) {
            return Response.fromText(
                    "Ваша карта устарела или по другим причинам не активна. Пожалуйста, удалите её и введите новую"
            );
        } else {
            log.error("Unknown status: {}", sodexoResponse.getStatus());
            return Response.fromText("Произошла ошибка, повторите попытку");
        }
    }
}
