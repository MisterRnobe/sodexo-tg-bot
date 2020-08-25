package ru.nmedvedev.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.nmedvedev.config.properties.TelegramBotProperties;
import ru.nmedvedev.service.converter.ResponseToEditMessageTextConverter;
import ru.nmedvedev.service.converter.ResponseToSendMessageConverter;
import ru.nmedvedev.view.Response;

import java.io.Serializable;
import java.time.Duration;


@Slf4j
@RequiredArgsConstructor
public class TelegramService extends TelegramLongPollingBot {

    private final ResponseToSendMessageConverter responseToSendMessageConverter;
    private final TelegramBotProperties telegramBotProperties;
    private final CallbackResolver callbackResolver;

    @Override
    @SneakyThrows
    public void onUpdateReceived(Update update) {
        log.info("Received update {}", update);
        if (update.hasMessage()) {
            if (update.getMessage().hasText()) {
                var chatId = update.getMessage().getChatId();
                var text = update.getMessage().getText();
                callbackResolver.getTextHandler(text)
                        .orElseGet(callbackResolver::defaultTextHandler)
                        .handle(chatId, text)
                        .map(response -> responseToSendMessageConverter.convert(response, chatId))
                        .map(this::executeSafe)
                        .await()
                        .atMost(Duration.ofSeconds(10L));
            } else {
                // do nothing
            }
        } else {
            log.warn("Unexpected event...");
        }
    }

    @Override
    public String getBotUsername() {
        return telegramBotProperties.getBotUsername();
    }

    @Override
    public String getBotToken() {
        return telegramBotProperties.getBotToken();
    }

    @SneakyThrows
    private <T extends Serializable, Method extends BotApiMethod<T>> T executeSafe(Method m) {
        return this.execute(m);
    }

    public void sendMessage(long chatId, Response response) {
        executeSafe(responseToSendMessageConverter.convert(response, chatId));
    }
}
