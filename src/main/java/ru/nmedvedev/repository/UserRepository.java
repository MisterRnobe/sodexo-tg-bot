package ru.nmedvedev.repository;

import io.quarkus.mongodb.panache.reactive.ReactivePanacheMongoRepository;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import org.bson.Document;
import ru.nmedvedev.model.UserDb;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;


@ApplicationScoped
public class UserRepository implements ReactivePanacheMongoRepository<UserDb> {

    public Uni<UserDb> findByChatId(Long chatId) {
        return find("chatId", chatId).firstResult();
    }

    public Multi<UserDb> findSubscribedWithCard() {
        return find(new Document(Map.of(
                "subscribed", true,
                "card", new Document("$ne", null))))
                .stream();
    }
}
