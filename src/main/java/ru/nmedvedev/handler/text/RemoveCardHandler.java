package ru.nmedvedev.handler.text;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import ru.nmedvedev.handler.InputTextHandler;
import ru.nmedvedev.repository.UserRepository;
import ru.nmedvedev.view.Response;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RequiredArgsConstructor
public class RemoveCardHandler implements InputTextHandler {

    private final UserRepository userRepository;

    @Override
    public String getPattern() {
        // TODO: 14/08/2020 Replace with localization service
        return "Удали карту";
    }

    @Override
    public Uni<Response> handle(Long chatId, String text) {
        return userRepository.findByChatId(chatId)
                .onItem().ifNotNull().transformToUni(userDb -> {
                    if (userDb.getCard() == null) {
                        // TODO: 14/08/2020 should be error?
                        return Uni.createFrom().item(Response.fromText("Вы не задали карту"));
                    } else {
                        var card = userDb.getCard();
                        userDb.setCard(null);
                        return userRepository.persistOrUpdate(userDb)
                                .map(result -> Response.fromText("Карта " + card + " удалена, введите новую"));
                    }
                })
                .onItem().ifNull().continueWith(() -> Response.fromText("Вы не задали карту"));
    }
}
