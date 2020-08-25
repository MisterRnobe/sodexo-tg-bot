package ru.nmedvedev.service.converter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import ru.nmedvedev.view.Response;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.nmedvedev.Helper.CHAT;

@ExtendWith(MockitoExtension.class)
class ResponseToSendMessageConverterTest {

    @InjectMocks
    private ResponseToSendMessageConverter converter;

    @Test
    void shouldConvertWithReplyButtons() {
        var response = Response.withReplyButtons("text", List.of("b1", "b2", "b3"));

        var actual = converter.convert(response, CHAT);

        var expected = new SendMessage()
                .setText("text")
                .setChatId(CHAT)
                .setReplyMarkup(new ReplyKeyboardMarkup(List.of(
                        keyboardRow("b1"),
                        keyboardRow("b2"),
                        keyboardRow("b3")
                )));
        assertEquals(expected, actual);
    }

    @MethodSource
    @ParameterizedTest
    void shouldConvertWithRemoveReplyKeyboardIfButtonsAreAbsent(Response response) {
        var actual = converter.convert(response, CHAT);

        var expected = new SendMessage()
                .setText("text")
                .setReplyMarkup(new ReplyKeyboardRemove())
                .setChatId(CHAT);
        assertEquals(expected, actual);
    }

    public static Stream<Response> shouldConvertWithRemoveReplyKeyboardIfButtonsAreAbsent() {
        return Stream.of(
                Response.withReplyButtons("text", null),
                Response.withReplyButtons("text", List.of())
        );
    }

    private KeyboardRow keyboardRow(String... keyNames) {
        var keyboardButtons = new KeyboardRow();
        for (var key: keyNames) {
            keyboardButtons.add(key);
        }
        return keyboardButtons;
    }
}
