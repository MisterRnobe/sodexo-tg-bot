package ru.nmedvedev.repository;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.smallrye.mutiny.Uni;
import ru.nmedvedev.model.UserDb;

import javax.enterprise.context.ApplicationScoped;


@ApplicationScoped
public class UserRepository implements ReactivePanacheMongoRepository<UserDb> {

    public Uni<UserDb> findByChatId(Long chatId) {
        return find("chatId", chatId).firstResult();
    }

}
