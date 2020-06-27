package ru.nmedvedev.model;

import lombok.Value;

import java.util.List;

@Value
public class Callback {

    String name;
    List<String> arguments;

}
