package ru.nmedvedev.rest;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import ru.nmedvedev.model.Balance;
import ru.nmedvedev.model.SodexoData;
import ru.nmedvedev.model.SodexoResponse;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SodexoCustomClient {

    private final SodexoClient sodexoClient;

    public SodexoCustomClient(@RestClient SodexoClient sodexoClient) {
        this.sodexoClient = sodexoClient;
    }

    public Uni<SodexoResponse> getByCard(String card) {
        return this.sodexoClient.getByCard(card);
    }

    public Uni<Double> getAmount(String card) {
        return sodexoClient.getByCard(card)
                .map(SodexoResponse::getData)
                .map(SodexoData::getBalance)
                .map(Balance::getAvailableAmount)
                .onFailure(throwable -> throwable instanceof NullPointerException)
                .recoverWithItem(() -> null)
                .onFailure()
                .retry()
                .atMost(3);
    }

}
