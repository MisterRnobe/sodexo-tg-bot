package ru.nmedvedev.config;

import io.quarkus.runtime.Startup;
import lombok.SneakyThrows;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import ru.nmedvedev.service.TelegramReceiver;

import javax.enterprise.context.ApplicationScoped;

@Startup
@ApplicationScoped
public class TelegramBotInitializer {

    static {
        // I myself do not like this solution https://github.com/rubenlagus/TelegramBots/issues/161
        ApiContextInitializer.init();
    }

    @SneakyThrows
    TelegramBotInitializer(TelegramReceiver telegramReceiver) {
        var telegramBotsApi = new TelegramBotsApi();
        telegramBotsApi.registerBot(telegramReceiver);
    }
}
