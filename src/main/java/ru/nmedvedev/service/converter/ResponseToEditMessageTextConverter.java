package ru.nmedvedev.service.converter;

import lombok.RequiredArgsConstructor;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import ru.nmedvedev.view.Response;

import javax.enterprise.context.ApplicationScoped;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@ApplicationScoped
@RequiredArgsConstructor
public class ResponseToEditMessageTextConverter {

    private final ButtonToInlineKeyboardButtonConverter converter;

    public EditMessageText convert(Response response, Long chatId) {
        var buttons = Optional.of(response)
                .map(Response::getButtons)
                .stream()
                .flatMap(Collection::stream)
                .map(list ->
                        list.stream()
                                .map(converter::convert)
                                .collect(Collectors.toUnmodifiableList()))
                .collect(Collectors.toList());

        var editMessageText = new EditMessageText();

        if (!buttons.isEmpty()) {
            editMessageText.setReplyMarkup(new InlineKeyboardMarkup(buttons));
        }

        return editMessageText
                .setText(response.getText())
                .setChatId(chatId);
    }
}
