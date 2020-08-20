package ru.nmedvedev.view;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ReplyButtonsProvider {
    public List<String> provideMenuButtons() {
        return List.of(
                "Покажи баланс",
                "Удали карту",
                "Подпиши или отпиши на уведомления о балансе",
                "Покажи мою карту"
        );
    }
}
