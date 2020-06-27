package ru.nmedvedev.view.service;

import ru.nmedvedev.view.HistoryView;
import ru.nmedvedev.view.Response;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class HistoryPageProvider {

    public Response provide(List<HistoryView> historyViews) {
        return new Response();
    }

}
