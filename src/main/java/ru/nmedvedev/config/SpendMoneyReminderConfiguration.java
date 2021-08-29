package ru.nmedvedev.config;


import io.smallrye.config.ConfigMapping;
import lombok.Data;

@Data
@ConfigMapping(prefix = "spend-money-reminder")
public class SpendMoneyReminderConfiguration {

    double halfMonthAllowedBalance;
    double lastWorkingDayMinusFourAllowedBalance;
    double lastWorkingDayMinusThreeAllowedBalance;
    double lastWorkingDayMinusOneAllowedBalance;

}
