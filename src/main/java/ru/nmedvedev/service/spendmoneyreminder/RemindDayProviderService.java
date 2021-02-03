package ru.nmedvedev.service.spendmoneyreminder;

import javax.enterprise.context.ApplicationScoped;
import java.time.DayOfWeek;
import java.time.LocalDate;

@ApplicationScoped
public class RemindDayProviderService {

    public ReminderDayEnum getDay(LocalDate currentDate) {
        int currentDayOfMonth = currentDate.getDayOfMonth();
        if (currentDayOfMonth == 15) return ReminderDayEnum.HALF_MONTH;

        int lastDayOfMonth = currentDate.lengthOfMonth();
        int lastWorkingDayOfMonth;
        DayOfWeek lastDayOfTheMonth = LocalDate.of(currentDate.getYear(), currentDate.getMonth(), lastDayOfMonth).getDayOfWeek();
        switch (lastDayOfTheMonth) {
            case SATURDAY: lastWorkingDayOfMonth = lastDayOfMonth - 1; break;
            case SUNDAY: lastWorkingDayOfMonth = lastDayOfMonth - 2; break;
            default: lastWorkingDayOfMonth = lastDayOfMonth;
        }

        if (currentDayOfMonth == lastWorkingDayOfMonth - 1) return ReminderDayEnum.LAST_WORKING_DAY_MINUS_ONE;
        if (currentDayOfMonth == lastWorkingDayOfMonth - 4) return ReminderDayEnum.LAST_WORKING_DAY_MINUS_FOUR;
        if (currentDayOfMonth == lastWorkingDayOfMonth - 3) return ReminderDayEnum.LAST_WORKING_DAY_MINUS_TREE;

        return ReminderDayEnum.NOT_A_DAY_FOR_A_REMINDER;
    }

}
