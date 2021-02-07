package ru.nmedvedev.config;


import io.quarkus.arc.config.ConfigProperties;
import lombok.Data;

@ConfigProperties(prefix = "spend-money-reminder")
@Data
public class SpendMoneyReminderConfiguration {

    double halfMonthAllowedBalance;
    double lastWorkingDayMinusFourAllowedBalance;
    double lastWorkingDayMinusThreeAllowedBalance;
    double lastWorkingDayMinusOneAllowedBalance;

}
