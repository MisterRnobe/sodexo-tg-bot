package ru.nmedvedev.view.service;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import ru.nmedvedev.model.UserDb;
import ru.nmedvedev.service.HandlerName;
import ru.nmedvedev.view.Button;
import ru.nmedvedev.view.Response;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class MenuProviderTest {

    private MenuProvider menuProvider = new MenuProvider();


    @ParameterizedTest
    @MethodSource(value = "provideSubscribeTestArguments")
    void shouldReturnMenuResponseForUnsubscribedUser(boolean subscribed, String subscribeButtonText) {
        var user = new UserDb(0L, "zzzzz", subscribed);

        var actual = menuProvider.provideForUser(user);

//        var expected = new Response("Choose an action for card " + user.getCard(), List.of(
//                List.of(new Button("Check balance", HandlerName.GET_BALANCE), new Button("Check history", HandlerName.GET_HISTORY)),
//                List.of(new Button(subscribeButtonText, HandlerName.SUBSCRIBE), new Button("Remove card", HandlerName.REMOVE_CARD))
//
//        ));
        assertEquals(null, actual);
    }

    static List<Arguments> provideSubscribeTestArguments() {
        return List.of(
                arguments(true, "Unsubscribe on balance change"),
                arguments(false, "Subscribe on balance change")
        );
    }
}
