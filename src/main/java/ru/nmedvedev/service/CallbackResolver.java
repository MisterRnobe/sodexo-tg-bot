package ru.nmedvedev.service;

import lombok.RequiredArgsConstructor;
import ru.nmedvedev.handler.ButtonClickHandler;
import ru.nmedvedev.handler.InputTextHandler;
import ru.nmedvedev.handler.text.DefaultHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import java.util.Optional;

@ApplicationScoped
@RequiredArgsConstructor
public class CallbackResolver {

    private final Instance<InputTextHandler> textHandlers;
    private final Instance<ButtonClickHandler> buttonClickHandlers;
    private final DefaultHandler defaultHandler;

    public Optional<InputTextHandler> getTextHandler(String text) {
        return textHandlers.stream()
                .filter(inputTextHandler -> text.matches(inputTextHandler.getPattern()))
                .findFirst();
    }

    public Optional<ButtonClickHandler> getButtonHandler(String callbackName) {
        return buttonClickHandlers
                .stream()
                .filter(buttonClickHandler -> buttonClickHandler.getName().equals(callbackName))
                .findFirst();
    }

    public InputTextHandler defaultTextHandler() {
        return defaultHandler;
    }
}
