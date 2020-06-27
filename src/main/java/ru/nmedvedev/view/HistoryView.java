package ru.nmedvedev.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryView {

    private String location;
    private double amount;
    private String currency;
    private String date;

}
