package ru.nmedvedev.model;

import io.quarkus.mongodb.panache.MongoEntity;
import io.quarkus.mongodb.panache.PanacheMongoEntity;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;


@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
@MongoEntity(collection = "userData")
public class UserDb extends PanacheMongoEntity {

    private Long chatId;
    private String card;
    private Boolean subscribed = false;
    private HistoryDb latestOperation = null;

    @Deprecated(forRemoval = true)
    public static UserDb withId(ObjectId id,
                                Long chatId,
                                String card,
                                Boolean subscribed) {
        var userDb = new UserDb(chatId, card, subscribed, null);
        userDb.id = id;
        return userDb;
    }


    public static UserDbBuilder builder() {
        return new UserDbBuilder();
    }

    public static class UserDbBuilder {

        private ObjectId id;
        private Long chatId;
        private String card;
        private Boolean subscribed;
        private HistoryDb latestOperation;

        UserDbBuilder() {
        }

        public UserDbBuilder latestOperation(HistoryDb latestOperation) {
            this.latestOperation = latestOperation;
            return this;
        }

        public UserDbBuilder chatId(Long chatId) {
            this.chatId = chatId;
            return this;
        }

        public UserDbBuilder card(String card) {
            this.card = card;
            return this;
        }

        public UserDbBuilder subscribed(Boolean subscribed) {
            this.subscribed = subscribed;
            return this;
        }

        public UserDbBuilder id(ObjectId id) {
            this.id = id;
            return this;
        }

        public UserDb build() {
            var userDb = new UserDb(chatId, card, subscribed, latestOperation);
            userDb.id = id;
            return userDb;
        }

        public String toString() {
            return "UserDb.UserDbBuilder(chatId=" + this.chatId + ", card=" + this.card + ", subscribed=" + this.subscribed + ")";
        }
    }
}
