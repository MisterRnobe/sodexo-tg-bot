package ru.nmedvedev.handler.button;

import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nmedvedev.model.UserDb;
import ru.nmedvedev.repository.UserRepository;
import ru.nmedvedev.view.Response;
import ru.nmedvedev.view.service.MenuProvider;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscribeHandlerTest {

    @InjectMocks
    private SubscribeHandler subscribeHandler;

    @Mock
    private UserRepository userDataRepository;
    @Mock
    private MenuProvider menuProvider;


    @ParameterizedTest
    @ValueSource(booleans = {true, false})
    void shouldSwitchSubscribedStatusAndSaveToDatabase(boolean subscribed) {
        var chatId = 0L;
        var card = "zzzzz";
        Response menuResponse = null;
//                new Response("Menu text", List.of());

        when(userDataRepository.findByChatId(chatId))
                .thenReturn(Uni.createFrom().item(new UserDb(chatId, card, subscribed)));
        when(menuProvider.provideForUser(new UserDb(chatId, card, !subscribed)))
                .thenReturn(menuResponse);

        var actual = subscribeHandler.handleWithArgs(chatId);

        assertEquals(menuResponse, actual.await().indefinitely());
        verify(userDataRepository, times(1)).persistOrUpdate(new UserDb(chatId, card, !subscribed));
    }
}
