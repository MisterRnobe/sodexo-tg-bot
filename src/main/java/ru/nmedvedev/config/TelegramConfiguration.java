package ru.nmedvedev.config;

import ru.nmedvedev.config.properties.TelegramBotProperties;
import ru.nmedvedev.handler.InputTextHandler;
import ru.nmedvedev.service.CallbackResolver;
import ru.nmedvedev.service.TelegramService;
import ru.nmedvedev.service.converter.ResponseToSendMessageConverter;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import java.util.ArrayList;
import java.util.Map;

@Dependent
public class TelegramConfiguration {

    @Produces
    @Dependent
    TelegramService telegramController(ResponseToSendMessageConverter responseToSendMessageConverter,
                                       TelegramBotProperties telegramBotProperties,
                                       CallbackResolver callbackResolver) {
        new ArrayList<InputTextHandler>();
        return new TelegramService(
                responseToSendMessageConverter,
                telegramBotProperties,
                callbackResolver
        );
    }


    @Produces
    Map<String, String> currencyTranslation() {
        return Map.of("RUR", "руб");
    }
}
