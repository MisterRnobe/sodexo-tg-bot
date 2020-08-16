package ru.nmedvedev.handler.text;

import io.smallrye.mutiny.Uni;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nmedvedev.model.UserDb;
import ru.nmedvedev.repository.UserRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static ru.nmedvedev.Helper.CARD;
import static ru.nmedvedev.Helper.CHAT;

@ExtendWith(MockitoExtension.class)
class RemoveCardHandlerTest {

    @InjectMocks
    private RemoveCardHandler removeCardHandler;
    @Mock
    private UserRepository userRepository;

    @Test
    void shouldRemoveCardForUserAndReturnRemovedMessage() {
        when(userRepository.findByChatId(CHAT))
                .thenReturn(Uni.createFrom().item(UserDb.builder().chatId(CHAT).card(CARD).build()));
        when(userRepository.persistOrUpdate((UserDb) any()))
                .thenReturn(Uni.createFrom().item(() -> null));

        var actual = removeCardHandler.handle(CHAT, "").await().indefinitely();

        assertEquals("Карта " + CARD + " удалена, введите новую", actual.getText());
        verify(userRepository, times(1))
                .persistOrUpdate(UserDb.builder().chatId(CHAT).build());
    }

    @Test
    void shouldSaveNothingAndReturnCardIsNotSetMessageIfCardIsNotPresent() {
        when(userRepository.findByChatId(CHAT))
                .thenReturn(Uni.createFrom().item(new UserDb()));

        var actual = removeCardHandler.handle(CHAT, "").await().indefinitely();

        assertEquals("Вы не задали карту", actual.getText());
        verify(userRepository, never())
                .persistOrUpdate((UserDb) any());
    }

    @Test
    void shouldSaveNothingAndReturnCardIsNotSetMessageIfUserIsNotPresent() {
        when(userRepository.findByChatId(CHAT))
                .thenReturn(Uni.createFrom().item(() -> null));

        var actual = removeCardHandler.handle(CHAT, "").await().indefinitely();

        assertEquals("Вы не задали карту", actual.getText());
        verify(userRepository, never())
                .persistOrUpdate((UserDb) any());
    }
}
