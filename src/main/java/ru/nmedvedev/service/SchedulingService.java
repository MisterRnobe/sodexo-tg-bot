package ru.nmedvedev.service;

import io.smallrye.mutiny.Multi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.time.Duration;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class SchedulingService {

    private final BalanceChangeChecker checker;

    public void startBalanceChangeChecking(Duration every) {
        Multi.createFrom()
                .ticks()
                .every(every)
                .subscribe()
                .with(l -> checker.check());
    }

}
