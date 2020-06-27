package ru.nmedvedev.view.service;

import org.junit.jupiter.api.Test;
import ru.nmedvedev.service.HandlerName;
import ru.nmedvedev.view.Button;
import ru.nmedvedev.view.Response;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HistoryPageProviderTest {

    private HistoryPageProvider historyPageProvider = new HistoryPageProvider();

    @Test
    void shouldReturn5LastOperation() {
//        var expected = new Response(
//                "1. 2020-01-02T03:04:05.000+0300 location 1 100 RUR\n" +
//                        "2. 2020-01-02T03:04:05.000+0300 location 2 101 RUR\n" +
//                        "3. 2020-01-02T03:04:05.000+0300 location 3 102 RUR\n" +
//                        "4. 2020-01-02T03:04:05.000+0300 location 4 103 RUR\n" +
//                        "5. 2020-01-02T03:04:05.000+0300 location 5 104 RUR\n",
//                List.of(List.of(new Button("<", HandlerName.PREVIOUS_PAGE), new Button(">", HandlerName.NEXT_PAGE)))
//        );
    }

    @Test
    void shouldReturnWithoutPaginationButtonsIfHistorySizeIsLessThan5() {

    }

    @Test
    void shouldReturnSpecificMessageForIfHistorySizeIs0() {

    }
}
