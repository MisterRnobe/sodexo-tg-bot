package ru.nmedvedev.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nmedvedev.service.HandlerName;

import java.util.List;

@Data
@Deprecated
@NoArgsConstructor
@AllArgsConstructor
public class Button {

    private String text;
    private String handlerName;
    private List<String> args;

    @Deprecated
    public Button(String text, String handlerName) {
        this.text = text;
        this.handlerName = handlerName;
    }
}
