package ru.nmedvedev.model;

import io.quarkus.mongodb.panache.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.glassfish.grizzly.http.util.UDecoder;


@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection = "userData")
public class UserDb extends PanacheMongoEntity {

    private Long chatId;
    private String card;
    private Boolean subscribed = false;

    public static UserDb withId(ObjectId id,
                                Long chatId,
                                String card,
                                Boolean subscribed) {
        var userDb = new UserDb(chatId, card, subscribed);
        userDb.id = id;
        return userDb;
    }

}
