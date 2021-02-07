package ru.nmedvedev.config;


import io.quarkus.arc.config.ConfigProperties;
import lombok.Data;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ConfigProperties(prefix = "spend-money-reminder.allowed-ballance")
@Data
public class SpendMoneyReminderConfiguration {

    @ConfigProperty(name = "half-month")
    double halfMonthAllowedBalance;

    @ConfigProperty(name = "last-minus-four")
    double lastWorkingDayMinusFourAllowedBalance;

    @ConfigProperty(name = "last-minus-three")
    double lastWorkingDayMinusThreeAllowedBalance;

    @ConfigProperty(name = "last-minus-one")
    double lastWorkingDayMinusOneAllowedBalance;

}
