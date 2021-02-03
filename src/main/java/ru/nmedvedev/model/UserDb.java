package ru.nmedvedev.model;

import io.quarkus.mongodb.panache.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.bson.types.ObjectId;


@EqualsAndHashCode(exclude = "id")
@Data
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection = "userData")
@Builder
public class UserDb {

    private ObjectId id;
    private Long chatId;
    private String card;
    private Boolean subscribed = false;
    private boolean subscribedToSpendMoneyReminder = false;
    private HistoryDb latestOperation = null;

    @Deprecated(forRemoval = true)
    public static UserDb withId(ObjectId id,
                                Long chatId,
                                String card,
                                Boolean subscribed,
                                Boolean subscribedToSpendMoneyReminder) {
        var userDb = new UserDb(id, chatId, card, subscribed, subscribedToSpendMoneyReminder, null);
        userDb.id = id;
        return userDb;
    }
}
