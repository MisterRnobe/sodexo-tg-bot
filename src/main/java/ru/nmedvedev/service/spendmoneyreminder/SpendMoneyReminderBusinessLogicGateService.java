package ru.nmedvedev.service.spendmoneyreminder;

import lombok.RequiredArgsConstructor;
import ru.nmedvedev.config.SpendMoneyReminderConfiguration;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RequiredArgsConstructor
public class SpendMoneyReminderBusinessLogicGateService {

    private final SpendMoneyReminderConfiguration configuration;

    public boolean needToSendNotification(ReminderDayEnum day, Double amount) {
        switch (day) {
            case NOT_A_DAY_FOR_A_REMINDER: return false;
            case HALF_MONTH: return amount >= configuration.getHalfMonthAllowedBalance();
            case LAST_WORKING_DAY_MINUS_FOUR: return amount >= configuration.getLastWorkingDayMinusFourAllowedBalance();
            case LAST_WORKING_DAY_MINUS_TREE: return amount >= configuration.getLastWorkingDayMinusThreeAllowedBalance();
            case LAST_WORKING_DAY_MINUS_ONE: return amount >= configuration.getLastWorkingDayMinusOneAllowedBalance();
            default: throw new IllegalArgumentException("Unknown day type");
        }
    }
}