package ru.nmedvedev.handler.text;

import io.smallrye.mutiny.Uni;
import ru.nmedvedev.handler.InputTextHandler;
import ru.nmedvedev.view.Response;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class StartCommandHandler implements InputTextHandler {


    @Override
    public String getPattern() {
        return "^\\/start$";
    }

    @Override
    public Uni<Response> handle(Long chatId, String text) {
        return Uni.createFrom().item(Response.fromText("Пожалуйста, введите номер вашей карты"));
    }
}
