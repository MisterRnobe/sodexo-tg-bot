package ru.nmedvedev.view;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Response {

    private String text;
    private List<List<Button>> buttons;
    private List<String> replyButtons;

    @Deprecated
    public Response(String text) {
        this.text = text;
    }

    public static Response fromText(String text) {
        return new Response(text, List.of(), List.of());
    }

    public static Response withKeyboardButton(String text, List<String> replyButtons) {
        return new Response(text, List.of(), replyButtons);
    }
}
