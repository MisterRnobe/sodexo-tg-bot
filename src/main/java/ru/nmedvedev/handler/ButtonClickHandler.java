package ru.nmedvedev.handler;

import io.smallrye.mutiny.Uni;
import ru.nmedvedev.view.Response;

import java.util.List;

public interface ButtonClickHandler {

    String getName();

    @Deprecated
    Uni<Response> handleWithArgs(Long chatId, String... args);

    default Uni<Response> handleWithArgs(Long chatId, List<String> args) {
        return handleWithArgs(chatId, args.toArray(new String[0]));
    }

}
