package ru.nmedvedev.service;

import io.smallrye.mutiny.Multi;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;

@Slf4j
@ApplicationScoped
public class SchedulingService {

    private final BalanceChangeChecker checker;
    private final Duration balanceCheckInterval;

    public SchedulingService(BalanceChangeChecker checker,
                             @ConfigProperty(name = "balance-check.every") Duration balanceCheckInterval) {
        this.checker = checker;
        this.balanceCheckInterval = balanceCheckInterval;
    }

    public void startBalanceChangeChecking() {
        Multi.createFrom().ticks().every(balanceCheckInterval)
                .onItem().invoke(tick -> log.info("Tick {} triggered", tick))
                .onItem().transformToMultiAndMerge(tick -> checker.check())
                .onFailure().retry().indefinitely()
                .subscribe().with(item -> log.info("User {} processed", item.getKey().getChatId()));
    }
}
