package ru.nmedvedev.view;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ReplyButtonsProvider {
    public List<String> provideMenuButtons() {
        return List.of(
                "Покажи баланс",
                "Удали карту",
                "Подписаться или отписаться на изменение баланса",
                "Покажи мою карту"
        );
    }
}
