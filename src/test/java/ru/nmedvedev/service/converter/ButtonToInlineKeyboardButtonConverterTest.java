package ru.nmedvedev.service.converter;

import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.nmedvedev.view.Button;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ButtonToInlineKeyboardButtonConverterTest {

    private ButtonToInlineKeyboardButtonConverter converter = new ButtonToInlineKeyboardButtonConverter();

    @Test
    void shouldConvertWithArgs() {
        var actual = converter.convert(new Button("SOME_TEXT", "handler", List.of("param1", "param2")));
        var expected = new InlineKeyboardButton("SOME_TEXT").setCallbackData("handler_param1_param2");
        assertEquals(expected, actual);
    }

    @Test
    void shouldConvertWithNoArgs() {
        var actual = converter.convert(new Button("SOME_TEXT", "handler", List.of()));
        var expected = new InlineKeyboardButton("SOME_TEXT").setCallbackData("handler");
        assertEquals(expected, actual);
    }
}
