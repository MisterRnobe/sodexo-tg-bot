package ru.nmedvedev.view;

import static ru.nmedvedev.handler.text.SpendMoneyReminderHandler.SPEND_MONEY_REMINDER_BUTTON_TEXT;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class ReplyButtonsProvider {
    public List<String> provideMenuButtons() {
        return List.of(
                "Покажи баланс",
                "Удали карту",
                "Подпиши или отпиши на уведомления о балансе",
                SPEND_MONEY_REMINDER_BUTTON_TEXT,
                "Покажи мою карту"
        );
    }
}
