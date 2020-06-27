package ru.nmedvedev.handler.button;

import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nmedvedev.model.History;
import ru.nmedvedev.model.SodexoData;
import ru.nmedvedev.model.SodexoResponse;
import ru.nmedvedev.rest.SodexoClient;
import ru.nmedvedev.view.HistoryView;
import ru.nmedvedev.view.Response;
import ru.nmedvedev.view.service.HistoryPageProvider;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HistoryHandlerTest {

    @InjectMocks
    private HistoryHandler historyHandler;

    @Mock
    private SodexoClient sodexoClient;
    @Mock
    private HistoryPageProvider historyPageProvider;

    private static final String CARD = "123";
    private static final Long CHAT = 0L;
    public static final OffsetDateTime BASE_TIME = OffsetDateTime.of(2020, 1, 2, 3, 4, 5, 0, ZoneOffset.UTC);

    @Test
    void shouldPassHistoryToProviderAndReturnItsResult() {
        when(sodexoClient.getByCard(CARD))
                .thenReturn(Uni.createFrom().item(getResponse()));
        when(historyPageProvider.provide(any()))
                .thenReturn(Response.fromText("History page"));

        var actual = historyHandler.handleWithArgs(CHAT, CARD);

        assertEquals(actual.await().indefinitely(), Response.fromText("History page"));
        verify(historyPageProvider, times(1))
                .provide(List.of(
                        new HistoryView("location 1", 100, "RUR", BASE_TIME.toString()),
                        new HistoryView("location 2", 101, "RUR", BASE_TIME.minusDays(1).toString()),
                        new HistoryView("location 3", 102, "RUR", BASE_TIME.minusDays(2).toString()),
                        new HistoryView("location 4", 103, "RUR", BASE_TIME.minusDays(3).toString()),
                        new HistoryView("location 5", 104, "RUR", BASE_TIME.minusDays(4).toString())
                ));
    }

    private SodexoResponse getResponse() {
        var sodexoData = new SodexoData();

        sodexoData.setHistory(List.of(
                History.builder().time(BASE_TIME.toString()).currency("RUR").amount(-100).locationName(List.of("location 1")).build(),
                History.builder().time(BASE_TIME.minusDays(1).toString()).currency("RUR").amount(-101).locationName(List.of("location 2")).build(),
                History.builder().time(BASE_TIME.minusDays(2).toString()).currency("RUR").amount(-102).locationName(List.of("location 3")).build(),
                History.builder().time(BASE_TIME.minusDays(3).toString()).currency("RUR").amount(-103).locationName(List.of("location 4")).build(),
                History.builder().time(BASE_TIME.minusDays(4).toString()).currency("RUR").amount(-104).locationName(List.of("location 5")).build(),
                History.builder().time(BASE_TIME.minusDays(5).toString()).currency("RUR").amount(-105).locationName(List.of("location 6")).build(),
                History.builder().time(BASE_TIME.minusDays(6).toString()).currency("RUR").amount(-106).locationName(List.of("location 7")).build()
        ));
        return new SodexoResponse(
                "OK",
                sodexoData
        );
    }
}
