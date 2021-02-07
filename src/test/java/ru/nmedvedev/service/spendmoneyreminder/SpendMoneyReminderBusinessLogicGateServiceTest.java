package ru.nmedvedev.service.spendmoneyreminder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.nmedvedev.config.SpendMoneyReminderConfiguration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SpendMoneyReminderBusinessLogicGateServiceTest {

    @InjectMocks
    private SpendMoneyReminderBusinessLogicGateService service;
    @Mock
    private SpendMoneyReminderConfiguration configuration;

    @Test
    void fireReminderOnHalfMonthIfAmountIsGreaterThanHalf() {
        when(configuration.getHalfMonthAllowedBalance()).thenReturn(1500.);
        assertTrue(service.needToSendNotification(ReminderDayEnum.HALF_MONTH, 1501.));
        assertFalse(service.needToSendNotification(ReminderDayEnum.HALF_MONTH, 1400.));
    }

    @Test
    void fireReminderOnWorkingDayMinus4() {
        when(configuration.getLastWorkingDayMinusFourAllowedBalance()).thenReturn(1000.);
        assertTrue(service.needToSendNotification(ReminderDayEnum.LAST_WORKING_DAY_MINUS_FOUR, 1000.));
        assertFalse(service.needToSendNotification(ReminderDayEnum.LAST_WORKING_DAY_MINUS_FOUR, 900.));
    }

    @Test
    void fireReminderOnHalfDayMinus3() {
        when(configuration.getLastWorkingDayMinusThreeAllowedBalance()).thenReturn(500.);
        assertTrue(service.needToSendNotification(ReminderDayEnum.LAST_WORKING_DAY_MINUS_TREE, 500.));
        assertFalse(service.needToSendNotification(ReminderDayEnum.LAST_WORKING_DAY_MINUS_TREE, 400.));
    }

    @Test
    void fireReminderOnLastDay() {
        when(configuration.getLastWorkingDayMinusOneAllowedBalance()).thenReturn(400.);
        assertTrue(service.needToSendNotification(ReminderDayEnum.LAST_WORKING_DAY_MINUS_ONE, 400.));
        assertFalse(service.needToSendNotification(ReminderDayEnum.LAST_WORKING_DAY_MINUS_ONE, 200.));
    }

}