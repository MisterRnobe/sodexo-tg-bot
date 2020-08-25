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
    private List<String> replyButtons;

    @Deprecated
    public Response(String text) {
        this.text = text;
    }

    public static Response fromText(String text) {
        return new Response(text, List.of());
    }

    public static Response withReplyButtons(String text, List<String> replyButtons) {
        return new Response(text, replyButtons);
    }
}
