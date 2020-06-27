package ru.nmedvedev.view.service;

import ru.nmedvedev.model.UserDb;
import ru.nmedvedev.service.HandlerName;
import ru.nmedvedev.view.Button;
import ru.nmedvedev.view.Response;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@Deprecated
@ApplicationScoped
public class MenuProvider {

    public Response provideForUser(UserDb userData) {
        String subscribeButtonText = userData.getSubscribed()
                ? "Unsubscribe on balance change"
                : "Subscribe on balance change";
        return null;
    }
}
