package ru.nmedvedev.handler.text;

import org.junit.jupiter.api.Test;
import ru.nmedvedev.view.Response;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StartCommandHandlerTest {

    private StartCommandHandler startCommandHandler = new StartCommandHandler();

    @Test
    void shouldReturnProperTextForStartCommand() {
        var response = startCommandHandler.handle(0L, "");

        assertEquals(Response.fromText("Please, enter your Sodexo card number!"), response.await().indefinitely());
    }
}
