package ru.nmedvedev.config;

import io.quarkus.runtime.Startup;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import ru.nmedvedev.service.IndexVerifier;
import ru.nmedvedev.service.SchedulingService;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;

@Startup
@ApplicationScoped
public class ApplicationInitializer {

    ApplicationInitializer(IndexVerifier indexVerifier,
                           SchedulingService schedulingService,
                           @ConfigProperty(name = "scheduling.everySeconds") Long everySeconds) {
        indexVerifier.createIndexesIfNotExist();
        schedulingService.startBalanceChangeChecking(Duration.ofSeconds(everySeconds));
    }
}
