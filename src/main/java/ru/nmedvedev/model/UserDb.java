package ru.nmedvedev.model;

import io.quarkus.mongodb.panache.MongoEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
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
    private HistoryDb latestOperation;

}
