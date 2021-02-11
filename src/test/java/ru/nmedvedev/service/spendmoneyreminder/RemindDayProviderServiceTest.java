package ru.nmedvedev.service.spendmoneyreminder;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

class RemindDayProviderServiceTest {

    private final RemindDayProviderService remindDayProviderService = new RemindDayProviderService();

    private static Stream<Arguments> getArgs() {
        return Stream.of(
                // Half Month
                Arguments.arguments(2020, 1, 15, ReminderDayEnum.HALF_MONTH),
                // Last working days minus 4
                Arguments.arguments(2021, 9, 26, ReminderDayEnum.LAST_WORKING_DAY_MINUS_FOUR),
                Arguments.arguments(2021, 10, 25, ReminderDayEnum.LAST_WORKING_DAY_MINUS_FOUR),
                Arguments.arguments(2021, 11, 26, ReminderDayEnum.LAST_WORKING_DAY_MINUS_FOUR),
                Arguments.arguments(2021, 12, 27, ReminderDayEnum.LAST_WORKING_DAY_MINUS_FOUR),
                // Last Working day minus 3
                Arguments.arguments(2021, 5, 28, ReminderDayEnum.LAST_WORKING_DAY_MINUS_TREE),
                Arguments.arguments(2021, 6, 27, ReminderDayEnum.LAST_WORKING_DAY_MINUS_TREE),
                Arguments.arguments(2021, 7, 27, ReminderDayEnum.LAST_WORKING_DAY_MINUS_TREE),
                Arguments.arguments(2021, 8, 28, ReminderDayEnum.LAST_WORKING_DAY_MINUS_TREE),
                // Regular Day
                Arguments.arguments(2020, 1, 14, ReminderDayEnum.NOT_A_DAY_FOR_A_REMINDER),
                Arguments.arguments(2021, 2, 1, ReminderDayEnum.NOT_A_DAY_FOR_A_REMINDER),
                Arguments.arguments(2021, 1, 14, ReminderDayEnum.NOT_A_DAY_FOR_A_REMINDER),
                Arguments.arguments(2021, 3, 29, ReminderDayEnum.NOT_A_DAY_FOR_A_REMINDER),
                Arguments.arguments(2021, 5, 26, ReminderDayEnum.NOT_A_DAY_FOR_A_REMINDER),
                // Last working day minus one
                Arguments.arguments(2021, 1, 28, ReminderDayEnum.LAST_WORKING_DAY_MINUS_ONE),
                Arguments.arguments(2021, 2, 25, ReminderDayEnum.LAST_WORKING_DAY_MINUS_ONE),
                Arguments.arguments(2021, 3, 30, ReminderDayEnum.LAST_WORKING_DAY_MINUS_ONE),
                Arguments.arguments(2021, 4, 29, ReminderDayEnum.LAST_WORKING_DAY_MINUS_ONE)
        );
    }

    @ParameterizedTest
    @MethodSource("getArgs")
    void returnCorrectDay(final int year, final int month, final int dayOfMonth, final ReminderDayEnum reminderDayEnum) {
        assertThat(remindDayProviderService.getDay(LocalDate.of(year, month, dayOfMonth)), is(reminderDayEnum));
    }
}