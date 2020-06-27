package ru.nmedvedev.service;

import ru.nmedvedev.view.Button;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class BackButtonProvider {

    public Button get(List<String> args) {
        return new Button("Back", HandlerName.BACK_TO_MENU, args);
    }
}
