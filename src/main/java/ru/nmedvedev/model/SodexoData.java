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
public class SodexoData {

    private Balance balance;
    private int exceedLimit;
    private List<History> history;
    private int ownAmount;
    private String phone;

}
