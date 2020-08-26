package ru.nmedvedev.service.converter;

import com.pengrad.telegrambot.model.request.ReplyKeyboardMarkup;
import com.pengrad.telegrambot.model.request.ReplyKeyboardRemove;
import com.pengrad.telegrambot.request.SendMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nmedvedev.view.Response;

import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.nmedvedev.Helper.CHAT;

@ExtendWith(MockitoExtension.class)
class ResponseToSendMessageConverterTest {

    private static final String TEXT = "text";
    @InjectMocks
    private ResponseToSendMessageConverter converter;

    @Test
    void shouldConvertWithReplyButtons() {
        var response = Response.withReplyButtons(TEXT, List.of("b1", "b2", "b3"));

        var actual = converter.convert(response, CHAT);

        var expected = new SendMessage(CHAT, TEXT)
                .replyMarkup(new ReplyKeyboardMarkup(
                        new String[]{"b1"},
                        new String[]{"b2"},
                        new String[]{"b3"}
                ));
        assertEquals(CHAT, actual.getParameters().get("chat_id"));
        assertEquals(TEXT, actual.getParameters().get("text"));
        assertEquals(expected.getParameters().get("reply_markup").toString(), actual.getParameters().get("reply_markup").toString());
    }

    @MethodSource
    @ParameterizedTest
    void shouldConvertWithRemoveReplyKeyboardIfButtonsAreAbsent(Response response) {
        var actual = converter.convert(response, CHAT);

        assertEquals(CHAT, actual.getParameters().get("chat_id"));
        assertEquals(TEXT, actual.getParameters().get("text"));
        assertEquals(ReplyKeyboardRemove.class, actual.getParameters().get("reply_markup").getClass());
    }

    public static Stream<Response> shouldConvertWithRemoveReplyKeyboardIfButtonsAreAbsent() {
        return Stream.of(
                Response.withReplyButtons(TEXT, null),
                Response.withReplyButtons(TEXT, List.of())
        );
    }

}
