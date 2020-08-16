package ru.nmedvedev.handler.text;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import ru.nmedvedev.handler.InputTextHandler;
import ru.nmedvedev.model.UserDb;
import ru.nmedvedev.repository.UserRepository;
import ru.nmedvedev.view.ReplyButtonsProvider;
import ru.nmedvedev.view.Response;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RequiredArgsConstructor
public class NotificationSubscriptionHandler implements InputTextHandler {

    private final UserRepository userRepository;
    private final ReplyButtonsProvider replyButtonsProvider;

    @Override
    public String getPattern() {
        return "Подпиши на уведомления о балансе";
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
        return userRepository.persistOrUpdate(userDb)
                .map(v -> Response.withKeyboardButton(message, replyButtonsProvider.provideMenuButtons()));
    }
}
