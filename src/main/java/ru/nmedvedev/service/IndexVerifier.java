package ru.nmedvedev.service;

import com.mongodb.client.MongoClient;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import javax.enterprise.context.ApplicationScoped;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static java.util.function.Predicate.not;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class IndexVerifier {

    private final MongoClient mongoClient;
    private static final Map<String, Optional<IndexOptions>> INDEX_TO_INDEX_OPTIONS = Map.of(
            "chatId", Optional.of(new IndexOptions().unique(true)),
            "subscribed", Optional.empty()
    );

    public void createIndexesIfNotExist() {
        // TODO: 26/08/2020 There is no index creation via annotation support
        var mongoClientDatabase = mongoClient.getDatabase("sodexoBot");
        var mongoCollection = mongoClientDatabase.getCollection("userData");

        var indexesFields = StreamSupport.stream(mongoCollection.listIndexes().spliterator(), false)
                .map(index -> ((Document) index.get("key"))
                        .keySet()
                        .stream()
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("Key is not present for " + index.toJson())))
                .collect(Collectors.toSet());

        INDEX_TO_INDEX_OPTIONS.entrySet()
                .stream()
                .filter(not(entry -> indexesFields.contains(entry.getKey())))
                .forEach(entry -> {
                    log.info("Creating index for {}", entry.getKey());
                    if (entry.getValue().isPresent()) {
                        mongoCollection.createIndex(Indexes.ascending(entry.getKey()), entry.getValue().get());
                    } else {
                        mongoCollection.createIndex(Indexes.ascending(entry.getKey()));
                    }
                });
    }
}
