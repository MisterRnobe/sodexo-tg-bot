package ru.nmedvedev.handler;

import io.smallrye.mutiny.Uni;
import ru.nmedvedev.view.Response;

public interface InputTextHandler {

    String getPattern();

    Uni<Response> handle(Long chatId, String text);

}
