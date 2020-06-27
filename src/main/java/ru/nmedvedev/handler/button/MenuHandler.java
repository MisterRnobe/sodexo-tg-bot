package ru.nmedvedev.handler.button;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import ru.nmedvedev.handler.ButtonClickHandler;
import ru.nmedvedev.repository.UserRepository;
import ru.nmedvedev.service.HandlerName;
import ru.nmedvedev.view.Response;
import ru.nmedvedev.view.service.MenuProvider;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@RequiredArgsConstructor
@Deprecated
public class MenuHandler implements ButtonClickHandler {

    private final UserRepository userRepository;
    private final MenuProvider menuProvider;

    @Override
    public String getName() {
        return HandlerName.BACK_TO_MENU;
    }

    @Override
    public Uni<Response> handleWithArgs(Long chatId, String... args) {
        return userRepository.findByChatId(chatId)
                .map(menuProvider::provideForUser);
    }
}
