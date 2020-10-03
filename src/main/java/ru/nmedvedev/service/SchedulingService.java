package ru.nmedvedev.service;

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
                .onTermination().invoke(this::startBalanceChangeChecking)
                .subscribe().with(item -> log.info("Updated user {}", item));
    }

}
