package ru.nmedvedev.service.converter;

import com.pengrad.telegrambot.model.request.Keyboard;
import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendMessage;
import ru.nmedvedev.view.Response;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

@ApplicationScoped
public class ResponseToSendMessageConverter {

    public SendMessage convert(Response response, Long chatId) {
        var keyboard = Optional.of(response)
                .map(Response::getReplyButtons)
                .filter(Predicate.not(List::isEmpty))
                .stream()
                .flatMap(Collection::stream)
                .map(str -> new String[]{str})
                .toArray(String[][]::new);

        Keyboard replyKeyboard = new ReplyKeyboardRemove();
        if (keyboard.length != 0) {
            replyKeyboard = new ReplyKeyboardMarkup(keyboard);
        }

        return new SendMessage(chatId, response.getText())
                .replyMarkup(replyKeyboard);
    }
}
