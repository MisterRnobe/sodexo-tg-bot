package ru.nmedvedev.service.converter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import ru.nmedvedev.service.HandlerName;
import ru.nmedvedev.service.converter.ResponseToSendMessageConverter;
import ru.nmedvedev.view.Button;
import ru.nmedvedev.view.Response;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResponseToSendMessageConverterTest {

    @InjectMocks
    private ResponseToSendMessageConverter converter;
    @Mock
    private ButtonToInlineKeyboardButtonConverter buttonToInlineKeyboardButtonConverter;

    @Test
    void shouldProperlyConvertWithButtons() {
        var button1 = new Button("1", "2", List.of("3"));
        var button2 = new Button("4", "5", List.of("6"));
        var button3 = new Button("7", "8", List.of("9"));
        var button4 = new Button("10", "11", List.of("12"));

        var inlineButton1 = new InlineKeyboardButton("123");
        var inlineButton2 = new InlineKeyboardButton("456");
        var inlineButton3 = new InlineKeyboardButton("789");
        var inlineButton4 = new InlineKeyboardButton("101112");

        when(buttonToInlineKeyboardButtonConverter.convert(button1)).thenReturn(inlineButton1);
        when(buttonToInlineKeyboardButtonConverter.convert(button2)).thenReturn(inlineButton2);
        when(buttonToInlineKeyboardButtonConverter.convert(button3)).thenReturn(inlineButton3);
        when(buttonToInlineKeyboardButtonConverter.convert(button4)).thenReturn(inlineButton4);

        var response = new Response();
        response.setText("text");
        response.setButtons(List.of(
                List.of(button1, button2),
                List.of(button3, button4)
        ));

        var actual = converter.convert(response, 0L);

        var expected = new SendMessage()
                .setText("text")
                .setChatId(0L)
                .setReplyMarkup(
                        new InlineKeyboardMarkup(
                                List.of(
                                        List.of(inlineButton1, inlineButton2),
                                        List.of(inlineButton3, inlineButton4)
                                )
                        )
                );
        assertEquals(expected, actual);
    }

    @Test
    void shouldConvertWithoutKeyboardIfButtonsAreAbsent() {
        var response = new Response();
        response.setText("text");
        response.setButtons(null);

        var actual = converter.convert(response, 0L);

        var expected = new SendMessage()
                .setText("text")
                .setChatId(0L);
        assertEquals(expected, actual);
    }
}
