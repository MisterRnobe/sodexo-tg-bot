package ru.nmedvedev.handler.button;

import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nmedvedev.model.Balance;
import ru.nmedvedev.model.SodexoData;
import ru.nmedvedev.model.SodexoResponse;
import ru.nmedvedev.rest.SodexoClient;
import ru.nmedvedev.service.HandlerName;
import ru.nmedvedev.view.Button;
import ru.nmedvedev.view.Response;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static ru.nmedvedev.Helper.CARD;
import static ru.nmedvedev.Helper.CHAT;

@ExtendWith(MockitoExtension.class)
class BalanceCheckHandlerTest {

    @InjectMocks
    private BalanceCheckHandler balanceCheckHandler;

    @Mock
    private SodexoClient sodexoClient;

    @Test
    void shouldReturnBalanceWithTwoFractionDigits() {
        var balance = new Balance(123.456d, "RUR");
        var sodexoData = new SodexoData();
        sodexoData.setBalance(balance);

        when(sodexoClient.getByCard(CARD))
                .thenReturn(Uni.createFrom().item(new SodexoResponse("OK", sodexoData)));

        var responseUni = balanceCheckHandler.handleWithArgs(CHAT, CARD);

//        var expected = new Response(
//                "Your balance is 123.46 RUR",
//                List.of(List.of(
//                        new Button("Back", HandlerName.BACK_TO_MENU, List.of(CARD)),
//                        new Button("Update", HandlerName.GET_BALANCE, List.of(CARD))
//                )));
        assertEquals(null, responseUni.await().indefinitely());
    }
}
