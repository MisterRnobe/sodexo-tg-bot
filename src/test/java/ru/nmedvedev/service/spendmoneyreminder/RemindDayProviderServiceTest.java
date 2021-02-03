package ru.nmedvedev.service.spendmoneyreminder;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RemindDayProviderServiceTest {

    private RemindDayProviderService remindDayProviderService = new RemindDayProviderService();

    @Test
    void returnHalfOfTheMonth() {
        assertThat(remindDayProviderService.getDay(LocalDate.of(2020, 1, 15)), is(ReminderDayEnum.HALF_MONTH));
        assertThat(remindDayProviderService.getDay(LocalDate.of(2020, 1, 14)), is(ReminderDayEnum.NOT_A_DAY_FOR_A_REMINDER));
    }

    @Test
    void returnLastWorkingDayMinus4() {
        assertThat(remindDayProviderService.getDay(LocalDate.of(2021, 9, 26)), is(ReminderDayEnum.LAST_WORKING_DAY_MINUS_FOUR));
        assertThat(remindDayProviderService.getDay(LocalDate.of(2021, 10, 25)), is(ReminderDayEnum.LAST_WORKING_DAY_MINUS_FOUR));
        assertThat(remindDayProviderService.getDay(LocalDate.of(2021, 11, 26)), is(ReminderDayEnum.LAST_WORKING_DAY_MINUS_FOUR));
        assertThat(remindDayProviderService.getDay(LocalDate.of(2021, 12, 27)), is(ReminderDayEnum.LAST_WORKING_DAY_MINUS_FOUR));
    }

    @Test
    void returnLastWorkingDayMinus3() {
        assertThat(remindDayProviderService.getDay(LocalDate.of(2021, 5, 28)), is(ReminderDayEnum.LAST_WORKING_DAY_MINUS_TREE));
        assertThat(remindDayProviderService.getDay(LocalDate.of(2021, 6, 27)), is(ReminderDayEnum.LAST_WORKING_DAY_MINUS_TREE));
        assertThat(remindDayProviderService.getDay(LocalDate.of(2021, 7, 27)), is(ReminderDayEnum.LAST_WORKING_DAY_MINUS_TREE));
        assertThat(remindDayProviderService.getDay(LocalDate.of(2021, 8, 28)), is(ReminderDayEnum.LAST_WORKING_DAY_MINUS_TREE));
    }

    @Test
    void returnLastWorkingDayMinus1() {
        assertThat(remindDayProviderService.getDay(LocalDate.of(2021, 1, 28)), is(ReminderDayEnum.LAST_WORKING_DAY_MINUS_ONE));
        assertThat(remindDayProviderService.getDay(LocalDate.of(2021, 2, 25)), is(ReminderDayEnum.LAST_WORKING_DAY_MINUS_ONE));
        assertThat(remindDayProviderService.getDay(LocalDate.of(2021, 3, 30)), is(ReminderDayEnum.LAST_WORKING_DAY_MINUS_ONE));
        assertThat(remindDayProviderService.getDay(LocalDate.of(2021, 4, 29)), is(ReminderDayEnum.LAST_WORKING_DAY_MINUS_ONE));
    }

    @Test
    void getOtherDay() {
        assertThat(remindDayProviderService.getDay(LocalDate.of(2021, 2, 1)), is(ReminderDayEnum.NOT_A_DAY_FOR_A_REMINDER));
        assertThat(remindDayProviderService.getDay(LocalDate.of(2021, 1, 14)), is(ReminderDayEnum.NOT_A_DAY_FOR_A_REMINDER));
        assertThat(remindDayProviderService.getDay(LocalDate.of(2021, 3, 29)), is(ReminderDayEnum.NOT_A_DAY_FOR_A_REMINDER));
        assertThat(remindDayProviderService.getDay(LocalDate.of(2021, 5, 26)), is(ReminderDayEnum.NOT_A_DAY_FOR_A_REMINDER));
    }
}