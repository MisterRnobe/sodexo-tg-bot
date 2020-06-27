package ru.nmedvedev.service;

import org.junit.jupiter.api.Test;
import ru.nmedvedev.model.Callback;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HandlerArgumentParserTest {

    private HandlerArgumentParser parser = new HandlerArgumentParser();

    @Test
    void shouldParseWithArgs() {
        var actual = parser.parse("handler_param1_param2");
        assertEquals(new Callback("handler", List.of("param1", "param2")), actual);
    }

    @Test
    void shouldParseWithNoArgs() {
        var actual = parser.parse("handler");
        assertEquals(new Callback("handler", List.of()), actual);
    }
}
