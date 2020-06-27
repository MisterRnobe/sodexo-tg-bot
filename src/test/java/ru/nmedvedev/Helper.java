package ru.nmedvedev;

import java.util.Random;

public final class Helper {

    public static final String CARD = String.valueOf(Math.abs(new Random().nextLong()));
    public static final Long CHAT = new Random().nextLong();

    private Helper() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }
}
