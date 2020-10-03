package ru.nmedvedev.service;

import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.response.SendResponse;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import ru.nmedvedev.service.converter.ResponseToSendMessageConverter;
import ru.nmedvedev.view.Response;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import java.time.Duration;


@Slf4j
@Dependent
@RequiredArgsConstructor
public class TelegramService {

    private final ResponseToSendMessageConverter responseToSendMessageConverter;
    private final CallbackResolver callbackResolver;
    private final TelegramBot telegramBot;


    @PostConstruct
    public void startEventListening() {
        log.info("Subscribing to events");
        telegramBot.setUpdatesListener(list -> {
            list.forEach(TelegramService.this::onUpdateReceived);
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
        });
    }

    @SneakyThrows
    public void onUpdateReceived(Update update) {
        log.info("Received update {}", update);
        if (update.message() != null) {
            if (update.message().text() != null) {
                var chatId = update.message().chat().id();
                var text = update.message().text();
                callbackResolver.getTextHandler(text)
                        .orElseGet(callbackResolver::defaultTextHandler)
                        .handle(chatId, text)
                        .invoke(response -> sendMessage(chatId, response))
                        .await()
                        .atMost(Duration.ofSeconds(10L));
            } else {
                log.info("Update has no text");
                // do nothing
            }
        } else {
            log.info("Update has no message");
        }
    }

    // TODO: 04/10/2020 Should be uni
    public void sendMessage(long chatId, Response response) {
        var request = responseToSendMessageConverter.convert(response, chatId);
        SendResponse sendResponse = telegramBot.execute(request);

        if (!sendResponse.isOk()) {
            log.warn("Error occurred when sending message {}, response text: {}", request, sendResponse.message());
        }
    }
}
