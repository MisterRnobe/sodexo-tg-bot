package ru.nmedvedev.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.nmedvedev.config.properties.TelegramBotProperties;
import ru.nmedvedev.handler.ButtonClickHandler;
import ru.nmedvedev.handler.InputTextHandler;
import ru.nmedvedev.model.Callback;
import ru.nmedvedev.service.converter.ResponseToEditMessageTextConverter;
import ru.nmedvedev.service.converter.ResponseToSendMessageConverter;

import java.util.List;

import static org.mockito.Mockito.*;
import static ru.nmedvedev.Helper.CHAT;

@ExtendWith(MockitoExtension.class)
class TelegramServiceTest {

    private TelegramService telegramService;

    private ResponseToSendMessageConverter responseToSendMessageConverter;
    @Mock
    private TelegramBotProperties telegramBotProperties;
    @Mock
    private CallbackResolver callbackResolver;

    @BeforeEach
    void setUp() {
        telegramService = new TelegramService(
                responseToSendMessageConverter,
                telegramBotProperties,
                callbackResolver
        );
    }

    @Test
    void shouldGetTextHandlerAndInvoke() {
        var text = "123";
        var handler = mock(InputTextHandler.class);

        telegramService.onUpdateReceived(new Update());

        verify(callbackResolver, times(1))
                .getTextHandler(text);
        verify(handler, times(1))
                .handle(CHAT, text);
    }

    @Test
    void shouldInvokeErrorTextHandlerIfHandlerIsNotFound() {

    }

    @Test
    void shouldDoNothingIfButtonHandlerIsNotFound() {

    }
}
