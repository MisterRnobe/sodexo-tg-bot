package ru.nmedvedev.config;

import ru.nmedvedev.config.properties.TelegramBotProperties;
import ru.nmedvedev.handler.InputTextHandler;
import ru.nmedvedev.service.CallbackResolver;
import ru.nmedvedev.service.HandlerArgumentParser;
import ru.nmedvedev.service.converter.ResponseToEditMessageTextConverter;
import ru.nmedvedev.service.converter.ResponseToSendMessageConverter;
import ru.nmedvedev.service.TelegramService;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import java.util.ArrayList;
import java.util.Map;

@Dependent
public class TelegramConfiguration {

    @Produces
    @Dependent
    TelegramService telegramController(ResponseToEditMessageTextConverter responseToEditMessageTextConverter,
                                       ResponseToSendMessageConverter responseToSendMessageConverter,
                                       TelegramBotProperties telegramBotProperties,
                                       CallbackResolver callbackResolver,
                                       HandlerArgumentParser parser) {
        new ArrayList<InputTextHandler>();
        return new TelegramService(
                responseToEditMessageTextConverter,
                responseToSendMessageConverter,
                telegramBotProperties,
                callbackResolver,
                parser
        );
    }


    @Produces
    Map<String, String> currencyTranslation() {
        return Map.of("RUR", "руб");
    }
}
