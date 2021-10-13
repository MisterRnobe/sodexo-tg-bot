package ru.nmedvedev.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.model.Chat;
import com.pengrad.telegrambot.model.Message;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import io.smallrye.mutiny.Uni;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nmedvedev.handler.InputTextHandler;
import ru.nmedvedev.service.converter.ResponseToSendMessageConverter;
import ru.nmedvedev.view.Response;

import java.util.Map;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static ru.nmedvedev.Helper.CHAT;

@ExtendWith(MockitoExtension.class)
class TelegramServiceTest {

    @InjectMocks
    private TelegramService telegramService;
    @Mock
    private ResponseToSendMessageConverter responseToSendMessageConverter;
    @Mock
    private CallbackResolver callbackResolver;
    @Mock
    private TelegramBot telegramBot;

    @Test
    void shouldGetTextHandlerThenInvokeItAndSendResponseIfUpdateHasMessageAndText() {
        var text = "123";
        var handler = mock(InputTextHandler.class);
        var response = Response.fromText("123");

        when(handler.handle(anyLong(), anyString()))
                .thenReturn(Uni.createFrom().item(response));
        when(callbackResolver.getTextHandler(text))
                .thenReturn(Optional.of(handler));
        when(responseToSendMessageConverter.convert(response, CHAT))
                .thenReturn(new SendMessage(CHAT, text));

        telegramService.onUpdateReceived(getUpdateWith(Map.of(
                "message", getMessage(CHAT, text)
        )));

        verify(callbackResolver, times(1))
                .getTextHandler(text);
        verify(handler, times(1))
                .handle(CHAT, text);
        verify(responseToSendMessageConverter, times(1))
                .convert(response, CHAT);
        verify(telegramBot, times(1))
                .execute(eq(responseToSendMessageConverter.convert(response, CHAT)), any());
    }

    @Test
    void shouldCallDefaultHandlerIfHandlerIsNotFound() {
        var text = "123";
        var handler = mock(InputTextHandler.class);
        var response = Response.fromText("123");

        when(handler.handle(anyLong(), anyString()))
                .thenReturn(Uni.createFrom().item(response));
        when(callbackResolver.getTextHandler(text))
                .thenReturn(Optional.empty());
        when(callbackResolver.defaultTextHandler())
                .thenReturn(handler);
        when(responseToSendMessageConverter.convert(response, CHAT))
                .thenReturn(new SendMessage(CHAT, text));

        telegramService.onUpdateReceived(getUpdateWith(Map.of(
                "message", getMessage(CHAT, text)
        )));

        verify(callbackResolver, times(1))
                .getTextHandler(text);
        verify(callbackResolver, times(1))
                .defaultTextHandler();
        verify(handler, times(1))
                .handle(CHAT, text);
        verify(responseToSendMessageConverter, times(1))
                .convert(response, CHAT);
        verify(telegramBot, times(1))
                .execute(eq(responseToSendMessageConverter.convert(response, CHAT)), any());
    }

    @Test
    void shouldReturnErrorMessageIfFailed() {
        var text = "123";
        var handler = mock(InputTextHandler.class);
        var response = Response.fromText("Произошла ошибка, повторите попытку");

        when(handler.handle(anyLong(), anyString()))
                .thenReturn(Uni.createFrom().failure(new RuntimeException("Hello world")));
        when(callbackResolver.getTextHandler(text))
                .thenReturn(Optional.of(handler));
        when(responseToSendMessageConverter.convert(response, CHAT))
                .thenReturn(new SendMessage(CHAT, text));

        telegramService.onUpdateReceived(getUpdateWith(Map.of(
                "message", getMessage(CHAT, text)
        )));

        verify(callbackResolver, times(1))
                .getTextHandler(text);
        verify(handler, times(1))
                .handle(CHAT, text);
        verify(responseToSendMessageConverter, times(1))
                .convert(response, CHAT);
        verify(telegramBot, times(1))
                .execute(eq(responseToSendMessageConverter.convert(response, CHAT)), any());
    }

    @SneakyThrows
    private SendResponse sendResponse() {
        var constructor = SendResponse.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        return constructor.newInstance();
    }

    private Update getUpdateWith(Map<String, Object> fields) {
        var update = new Update();
        fields.forEach((field, value) -> {
            try {
                var declaredField = Update.class.getDeclaredField(field);
                declaredField.setAccessible(true);
                declaredField.set(update, value);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        });
        return update;
    }

    private Message getMessage(long chatId, String text) {
        var message = new Message();
        try {
            var chat = new Chat();
            var chatIdField = Chat.class.getDeclaredField("id");
            chatIdField.setAccessible(true);
            chatIdField.set(chat, chatId);

            var chatField = Message.class.getDeclaredField("chat");
            chatField.setAccessible(true);
            chatField.set(message, chat);

            var textField = Message.class.getDeclaredField("text");
            textField.setAccessible(true);
            textField.set(message, text);

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return message;
    }
}
