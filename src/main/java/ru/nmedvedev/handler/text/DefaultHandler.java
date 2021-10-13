package ru.nmedvedev.handler.text;

import io.smallrye.mutiny.Uni;
import lombok.SneakyThrows;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import ru.nmedvedev.handler.InputTextHandler;
import ru.nmedvedev.model.UserDb;
import ru.nmedvedev.repository.UserRepository;
import ru.nmedvedev.rest.Constants;
import ru.nmedvedev.rest.SodexoClient;
import ru.nmedvedev.view.ReplyButtonsProvider;
import ru.nmedvedev.view.Response;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;
import java.util.Optional;

@ApplicationScoped
public class DefaultHandler implements InputTextHandler {

    private final UserRepository userRepository;
    private final SodexoClient sodexoClient;
    private final ReplyButtonsProvider replyButtonsProvider;

    public DefaultHandler(UserRepository userRepository,
                          @RestClient SodexoClient sodexoClient,
                          ReplyButtonsProvider replyButtonsProvider) {
        this.userRepository = userRepository;
        this.sodexoClient = sodexoClient;
        this.replyButtonsProvider = replyButtonsProvider;
    }

    @Override
    public String getPattern() {
        return "^_$";
    }

    @Override
    @SneakyThrows
    public Uni<Response> handle(Long chatId, String text) {
        // TODO: 14/08/2020 move to reactive pipeline
        var byChatId = userRepository.findByChatId(chatId).await().atMost(Duration.ofSeconds(10L));

        if (byChatId != null && byChatId.getCard() != null) {
            return Uni.createFrom().item(Response.withReplyButtons("Неизвестный запрос :(", replyButtonsProvider.provideMenuButtons()));
        } else {
            if (text.matches("[0-9 ]+")) {
                var noSpacesCard = text.replaceAll(" ", "")
                        .replaceAll("\r", "")
                        .replaceAll("\n", "");

                return sodexoClient.getByCard(noSpacesCard)
                        .flatMap(sodexoResponse -> {
                            if (sodexoResponse.getStatus().equals(Constants.OK_STATUS)) {
                                var userDb = Optional.ofNullable(byChatId)
                                        .orElseGet(() ->
                                                UserDb.builder()
                                                        .chatId(chatId)
                                                        .build()
                                        );
                                userDb.setCard(noSpacesCard);
                                return userRepository.persistOrUpdate(userDb)
                                        .map(v -> Response.withReplyButtons("Я сохранил карту " + noSpacesCard, replyButtonsProvider.provideMenuButtons()));
                            } else if (sodexoResponse.getStatus().equals(Constants.CARD_IS_NOT_ACTIVE_STATUS)) {
                                return Uni
                                        .createFrom()
                                        .item(Response.fromText("Карта" + text + " устарела или не активна по другим причинам, повторите ввод"));
                            } else {
                                // TODO: 13/08/2020 use localization
                                return Uni
                                        .createFrom()
                                        .item(Response.fromText("Карты " + text + " не существует, повторите ввод"));
                            }
                        });
            } else {

                return Uni
                        .createFrom()
                        .item(Response.fromText("Неверный формат карты"));
            }
        }
    }

    static class InternalException extends RuntimeException {

        public InternalException(String message) {
            super(message);
        }
    }
}
