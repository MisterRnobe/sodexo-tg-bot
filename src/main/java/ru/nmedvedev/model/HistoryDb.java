package ru.nmedvedev.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HistoryDb {

    private Double amount;
    private String currency;
    private String locationName;
    // TODO: 16/08/2020 Convert to OffsetDateTime
    private String time;

}
