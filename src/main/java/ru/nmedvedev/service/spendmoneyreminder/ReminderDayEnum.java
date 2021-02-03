package ru.nmedvedev.service.spendmoneyreminder;

public enum ReminderDayEnum {
    NOT_A_DAY_FOR_A_REMINDER(""),
    HALF_MONTH("Дружок. Половина месяца прошло. У тебя осталось %.2f руб. Не забудь потратить)"),
    LAST_WORKING_DAY_MINUS_FOUR("На твоём счету %.2f руб. Через 3 дня они сгорят."),
    LAST_WORKING_DAY_MINUS_TREE("Предпоследний день, чтобы потратить свои %.2f руб."),
    LAST_WORKING_DAY_MINUS_ONE("Ахтунг, комрад! У тебя ещё %.2f руб. на карте, а сегодня последний день чтобы их потратить!!!");

    public final String messageFormat;

    ReminderDayEnum(String messageFormat) {
        this.messageFormat = messageFormat;
    }
}
