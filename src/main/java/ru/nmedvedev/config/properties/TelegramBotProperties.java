package ru.nmedvedev.config.properties;

import io.quarkus.arc.config.ConfigProperties;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ConfigProperties(prefix = "telegram.bot")
public interface TelegramBotProperties {

    @ConfigProperty(name = "token")
    String getBotToken();

    @ConfigProperty(name = "username")
    String getBotUsername();

}
