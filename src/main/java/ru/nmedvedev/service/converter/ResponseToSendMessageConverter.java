package ru.nmedvedev.service.converter;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.nmedvedev.view.Response;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class ResponseToSendMessageConverter {

    public SendMessage convert(Response response, Long chatId) {

        var keyboardRows = Optional.of(response)
                .map(Response::getReplyButtons)
                .filter(Predicate.not(List::isEmpty))
                .stream()
                .flatMap(Collection::stream)
                .map(elem -> {
                    var row = new KeyboardRow();
                    row.add(elem);
                    return row;
                })
                .collect(Collectors.toList());

        var sendMessage = new SendMessage(chatId, response.getText());
        if (!keyboardRows.isEmpty()) {
            sendMessage.setReplyMarkup(new ReplyKeyboardMarkup(keyboardRows));
        } else {
            sendMessage.setReplyMarkup(new ReplyKeyboardRemove());
        }

        return sendMessage;
    }
}
