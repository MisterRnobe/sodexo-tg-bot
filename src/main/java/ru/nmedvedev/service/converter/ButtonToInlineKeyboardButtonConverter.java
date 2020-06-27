package ru.nmedvedev.service.converter;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.nmedvedev.view.Button;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ButtonToInlineKeyboardButtonConverter {
    public InlineKeyboardButton convert(Button button) {
        var callbackData = button.getHandlerName() +
                (button.getArgs().isEmpty()
                        ? ""
                        : "_" + String.join("_", button.getArgs()));
        return new InlineKeyboardButton()
                .setCallbackData(callbackData)
                .setText(button.getText());
    }
}
