package ru.nmedvedev.service.spendmoneyreminder;

public class SpendMoneyReminderBusinessLogicGateService {

    public boolean needToSendNotification(ReminderDayEnum day, Double amount) {
        switch (day) {
            case NOT_A_DAY_FOR_A_REMINDER: return false;
            case HALF_MONTH_REMINDER: return amount >= 1500;
            case LAST_WORKING_DAY_MINUS_THREE: return amount >= 1000;
            case LAST_WORKING_DAY_MINUS_ONE: return amount >= 500;
            case LAST_WORKING_DAY: return amount >= 400;
            default: throw new IllegalArgumentException("Unknown day type");
        }
    }
}