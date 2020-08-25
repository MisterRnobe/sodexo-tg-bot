package ru.nmedvedev.service;

import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.nmedvedev.handler.InputTextHandler;
import ru.nmedvedev.service.converter.ResponseToSendMessageConverter;
import ru.nmedvedev.view.Response;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.Mockito.*;
import static ru.nmedvedev.Helper.CHAT;

@ExtendWith(MockitoExtension.class)
class TelegramServiceTest {

    @Mock
    private TelegramService telegramService;
    @Mock
    private CallbackResolver callbackResolver;

    @BeforeEach
    void setUp() {
        try {
            var callbackResolverField = TelegramService.class.getDeclaredField("callbackResolver");
            callbackResolverField.setAccessible(true);
            callbackResolverField.set(telegramService, callbackResolver);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        doCallRealMethod().when(telegramService).onUpdateReceived(any());
    }

    @Test
    void shouldGetTextHandlerAndInvoke() {
        var text = "123";
        var handler = mock(InputTextHandler.class);
        var response = Response.fromText("123");

        when(handler.handle(anyLong(), anyString()))
                .thenReturn(Uni.createFrom().item(response));

        when(callbackResolver.getTextHandler(text))
                .thenReturn(Optional.of(handler));

        telegramService.onUpdateReceived(getUpdateWith(Map.of(
                "message", getMessage(CHAT, text)
        )));

        verify(callbackResolver, times(1))
                .getTextHandler(text);
        verify(handler, times(1))
                .handle(CHAT, text);
        verify(telegramService, times(1))
                .sendMessage(CHAT, response);
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

        telegramService.onUpdateReceived(getUpdateWith(Map.of(
                "message", getMessage(CHAT, text)
        )));

        verify(callbackResolver, times(1))
                .getTextHandler(text);
        verify(callbackResolver, times(1))
                .defaultTextHandler();
        verify(handler, times(1))
                .handle(CHAT, text);
        verify(telegramService, times(1))
                .sendMessage(CHAT, response);
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
