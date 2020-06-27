package ru.nmedvedev.handler.button;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import ru.nmedvedev.handler.ButtonClickHandler;
import ru.nmedvedev.model.SodexoData;
import ru.nmedvedev.model.SodexoResponse;
import ru.nmedvedev.rest.SodexoClient;
import ru.nmedvedev.service.HandlerName;
import ru.nmedvedev.view.HistoryView;
import ru.nmedvedev.view.Response;
import ru.nmedvedev.view.service.HistoryPageProvider;

import javax.enterprise.context.ApplicationScoped;
import java.util.stream.Collectors;

@ApplicationScoped
public class HistoryHandler implements ButtonClickHandler {

    private final SodexoClient sodexoClient;
    private final HistoryPageProvider historyPageProvider;

    public HistoryHandler(@RestClient SodexoClient sodexoClient,
                          HistoryPageProvider historyPageProvider) {
        this.sodexoClient = sodexoClient;
        this.historyPageProvider = historyPageProvider;
    }

    @Override
    public String getName() {
        return HandlerName.GET_HISTORY;
    }

    @Override
    public Uni<Response> handleWithArgs(Long chatId, String... args) {
        var card = args[0];
        return sodexoClient.getByCard(card)
                .map(SodexoResponse::getData)
                .map(SodexoData::getHistory)
                .map(list -> list.stream()
                        .limit(5)
                        .map(h -> new HistoryView(h.getLocationName().get(0), Math.abs(h.getAmount()), h.getCurrency(), h.getTime()))
                        .collect(Collectors.toList()))
                .map(historyPageProvider::provide);
    }
}
