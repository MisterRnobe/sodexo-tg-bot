package ru.nmedvedev.service.spendmoneyreminder;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpendMoneyReminderBusinessLogicGateServiceTest {

    private SpendMoneyReminderBusinessLogicGateService service = new SpendMoneyReminderBusinessLogicGateService();

    @Test
    void fireReminderOnHalfMonthIfAmountIsGreaterThanHalf() {
        assertTrue(service.needToSendNotification(ReminderDayEnum.HALF_MONTH, 1501.));
        assertFalse(service.needToSendNotification(ReminderDayEnum.HALF_MONTH, 1400.));
    }

    @Test
    void fireReminderOnWorkingDayMinus3() {
        assertTrue(service.needToSendNotification(ReminderDayEnum.LAST_WORKING_DAY_MINUS_FOUR, 1000.));
        assertFalse(service.needToSendNotification(ReminderDayEnum.LAST_WORKING_DAY_MINUS_FOUR, 900.));
    }

    @Test
    void fireReminderOnHalfDayMinusOne() {
        assertTrue(service.needToSendNotification(ReminderDayEnum.LAST_WORKING_DAY_MINUS_TREE, 500.));
        assertFalse(service.needToSendNotification(ReminderDayEnum.LAST_WORKING_DAY_MINUS_TREE, 400.));
    }

    @Test
    void fireReminderOnLastDay() {
        assertTrue(service.needToSendNotification(ReminderDayEnum.LAST_WORKING_DAY_MINUS_ONE, 400.));
        assertFalse(service.needToSendNotification(ReminderDayEnum.LAST_WORKING_DAY_MINUS_ONE, 200.));
    }

}