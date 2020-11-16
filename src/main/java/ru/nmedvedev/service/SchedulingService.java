package ru.nmedvedev.service;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class SchedulingService {

    private final BalanceChangeChecker checker;

    public void startBalanceChangeChecking() {
        checker.check()
                .onTermination().invokeUni(this::reInvoke)
                .onFailure().invokeUni(this::printErrorAndReInvoke)
                .subscribe().with(item -> log.info("Updated user {}", item));
    }

    private Uni<Void> reInvoke(Throwable throwable, Boolean aBoolean) {
        return Uni.createFrom().item(() -> {
            startBalanceChangeChecking();
            return null;
        });
    }

    private Uni<Void> printErrorAndReInvoke(Throwable t) {
        return Uni.createFrom().item(() -> {
            log.error("Failed balance check process with error", t);
            startBalanceChangeChecking();
            return null;
        });
    }
}
