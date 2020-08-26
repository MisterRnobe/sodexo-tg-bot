package ru.nmedvedev.config;

import io.quarkus.runtime.Startup;
import ru.nmedvedev.service.IndexVerifier;

import javax.enterprise.context.ApplicationScoped;

@Startup
@ApplicationScoped
public class ApplicationInitializer {

    ApplicationInitializer(IndexVerifier indexVerifier) {
        indexVerifier.createIndexesIfNotExist();
    }
}
