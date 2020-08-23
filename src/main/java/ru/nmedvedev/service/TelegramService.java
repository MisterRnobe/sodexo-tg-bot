package ru.nmedvedev.service;

import io.smallrye.mutiny.Uni;
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

    private final ResponseToEditMessageTextConverter responseToEditMessageTextConverter;
    private final ResponseToSendMessageConverter responseToSendMessageConverter;
    private final TelegramBotProperties telegramBotProperties;
    private final CallbackResolver callbackResolver;
    private final HandlerArgumentParser argumentParser;

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
        } else if (update.hasCallbackQuery()) {
            var chatId = update.getCallbackQuery().getMessage().getChatId();
            var messageId = update.getCallbackQuery().getMessage().getMessageId();
            var callbackData = argumentParser.parse(update.getCallbackQuery().getData());
            callbackResolver.getButtonHandler(callbackData.getName())
                    .orElseThrow(IllegalStateException::new)
                    .handleWithArgs(chatId, callbackData.getArguments())
                    // TODO: 11/08/2020 move to inner method??
                    .map(response -> responseToEditMessageTextConverter.convert(response, chatId).setMessageId(messageId))
                    .map(this::executeSafe)
                    .await()
                    .atMost(Duration.ofSeconds(10L));

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
