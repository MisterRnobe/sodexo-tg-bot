package ru.nmedvedev.service;


import ru.nmedvedev.model.Callback;

import javax.enterprise.context.ApplicationScoped;
import java.util.Arrays;
import java.util.List;

@ApplicationScoped
public class HandlerArgumentParser {
    public Callback parse(String data) {
        var strings = data.split("_");
        return new Callback(strings[0], List.of(Arrays.copyOfRange(strings, 1, strings.length)));
    }
}
