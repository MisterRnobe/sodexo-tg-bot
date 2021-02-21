package ru.nmedvedev.config;

import io.quarkus.runtime.Startup;
import ru.nmedvedev.service.IndexVerifier;
import ru.nmedvedev.service.SchedulingService;

import javax.enterprise.context.ApplicationScoped;

@Startup
@ApplicationScoped
public class ApplicationInitializer {

    ApplicationInitializer(IndexVerifier indexVerifier,
                           SchedulingService schedulingService) {
        indexVerifier.createIndexesIfNotExist();
//        schedulingService.startBalanceChangeCheckingNonReactive();
        schedulingService.startBalanceChangeChecking();
    }
}
