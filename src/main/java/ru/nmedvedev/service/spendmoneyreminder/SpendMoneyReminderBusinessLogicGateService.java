package ru.nmedvedev.service.spendmoneyreminder;

public class SpendMoneyReminderBusinessLogicGateService {
    public boolean needToSendNotification(ReminderDayEnum day, Double amount) {
        return false;
    }
}
