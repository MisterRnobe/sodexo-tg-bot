package ru.nmedvedev.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class History {

    private double amount;
    private String currency;
    private List<String> locationName;
    private String mcc;
    private String merchantId;
    // TODO: 27/06/2020 Convert to OffsetDateTime
    private String time;
    private int trnType;


}
