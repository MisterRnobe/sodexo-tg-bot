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

    private ResponseToEditMessageTextConverter responseToEditMessageTextConverter;
    private ResponseToSendMessageConverter responseToSendMessageConverter;
    @Mock
    private TelegramBotProperties telegramBotProperties;
    @Mock
    private CallbackResolver callbackResolver;
    @Mock
    private HandlerArgumentParser argumentParser;

    @BeforeEach
    void setUp() {
        telegramService = new TelegramService(
                responseToEditMessageTextConverter,
                responseToSendMessageConverter,
                telegramBotProperties,
                callbackResolver,
                argumentParser
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
    void shouldGetButtonHandlerAndInvokeWithParsedArguments() {
        var callbackZzz = "callback_zzzz";
        var callbackData = new Callback("some_name", List.of("arg1", "arg2"));
        var handler = mock(ButtonClickHandler.class);

        telegramService.onUpdateReceived(new Update());

        verify(argumentParser, times(1))
                .parse(callbackZzz);
        verify(callbackResolver, times(1))
                .getButtonHandler(callbackData.getName());
        verify(handler, times(1))
                .handleWithArgs(CHAT, callbackData.getArguments());
    }

    @Test
    void shouldInvokeErrorTextHandlerIfHandlerIsNotFound() {

    }

    @Test
    void shouldDoNothingIfButtonHandlerIsNotFound() {

    }
}
