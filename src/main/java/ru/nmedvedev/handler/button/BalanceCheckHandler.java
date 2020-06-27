package ru.nmedvedev.handler.button;

import io.smallrye.mutiny.Uni;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import ru.nmedvedev.handler.ButtonClickHandler;
import ru.nmedvedev.repository.UserRepository;
import ru.nmedvedev.rest.SodexoClient;
import ru.nmedvedev.service.BackButtonProvider;
import ru.nmedvedev.service.HandlerName;
import ru.nmedvedev.view.Button;
import ru.nmedvedev.view.Response;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@Slf4j
@ApplicationScoped
@Deprecated
public class BalanceCheckHandler implements ButtonClickHandler {

    private final UserRepository userRepository;
    private final SodexoClient sodexoClient;
    private final BackButtonProvider backButtonProvider;

    public BalanceCheckHandler(UserRepository userRepository,
                               @RestClient SodexoClient sodexoClient,
                               BackButtonProvider backButtonProvider) {
        this.userRepository = userRepository;
        this.sodexoClient = sodexoClient;
        this.backButtonProvider = backButtonProvider;
    }

    @Override
    public String getName() {
        return HandlerName.GET_BALANCE;
    }

    @Override
    public Uni<Response> handleWithArgs(Long chatId, String... args) {
        var card = args[0];
        return sodexoClient.getByCard(card)
                .map(r -> {
                    if ("OK".equals(r.getStatus())) {
                        return r;
                    } else {
                        throw new IllegalStateException("Wrong status: " + r.getStatus());
                    }
                })
                .map(r -> {
                    var balanceMsg = String.format(
                            "Your balance is %.2f %s",
                            r.getData().getBalance().getAvailableAmount(),
                            r.getData().getBalance().getCurrency());
                    var buttons = List.of(List.of(
                            backButtonProvider.get(List.of(card)),
                            new Button("Update", HandlerName.GET_BALANCE, List.of(card))
                    ));
                    return Response.fromText(balanceMsg);
                });
    }
}
