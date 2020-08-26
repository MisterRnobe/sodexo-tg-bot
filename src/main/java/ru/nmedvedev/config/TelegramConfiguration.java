package ru.nmedvedev.config;

import com.pengrad.telegrambot.TelegramBot;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;

@Dependent
public class TelegramConfiguration {

    @Produces
    TelegramBot telegramBot(@ConfigProperty(name = "telegram.bot.token") String botToken) {
        return new TelegramBot(botToken);
    }
}
