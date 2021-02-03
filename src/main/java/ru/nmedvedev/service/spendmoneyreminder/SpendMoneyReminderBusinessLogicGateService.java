package ru.nmedvedev.service.spendmoneyreminder;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class SpendMoneyReminderBusinessLogicGateService {

    public boolean needToSendNotification(ReminderDayEnum day, Double amount) {
        switch (day) {
            case NOT_A_DAY_FOR_A_REMINDER: return false;
            case HALF_MONTH: return amount >= 1500;
            case LAST_WORKING_DAY_MINUS_FOUR: return amount >= 1000;
            case LAST_WORKING_DAY_MINUS_TREE: return amount >= 500;
            case LAST_WORKING_DAY_MINUS_ONE: return amount >= 400;
            default: throw new IllegalArgumentException("Unknown day type");
        }
    }
}