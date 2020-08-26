package ru.nmedvedev.config;

import io.quarkus.runtime.Startup;
import lombok.SneakyThrows;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import ru.nmedvedev.service.IndexVerifier;
import ru.nmedvedev.service.TelegramService;

import javax.enterprise.context.ApplicationScoped;

@Startup
@ApplicationScoped
public class ApplicationInitializer {

    static {
        // I myself do not like this solution https://github.com/rubenlagus/TelegramBots/issues/161
        ApiContextInitializer.init();
    }

    @SneakyThrows
    ApplicationInitializer(TelegramService telegramService,
                           IndexVerifier indexVerifier) {
        var telegramBotsApi = new TelegramBotsApi();
        telegramBotsApi.registerBot(telegramService);
        indexVerifier.createIndexesIfNotExist();
    }
}
