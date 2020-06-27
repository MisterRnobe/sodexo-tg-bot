package ru.nmedvedev.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SodexoResponse {

    private String status;
    private SodexoData data;

}
