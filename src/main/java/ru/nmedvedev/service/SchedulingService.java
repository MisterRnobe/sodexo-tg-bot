package ru.nmedvedev.service;

import io.smallrye.mutiny.Uni;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.context.ManagedExecutor;

import javax.enterprise.context.ApplicationScoped;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Function;

@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class SchedulingService {

    private final BalanceChangeChecker checker;
    private final ManagedExecutor executor;
    private final AtomicInteger invocationCounter = new AtomicInteger(0);

    private static final int PRINT_EVERY_COUNT = 1800;

    public void startBalanceChangeCheckingNonReactive() {
        try {
            executor
                    .runAsync(() ->{
                        while (true) {
                            try {
                                checker.checkNonReactive();
                            } catch (Exception e) {
                                log.error("Exception occurred", e);
                            } finally {
                                logCompletion().run();
                            }
                        }
                    })
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            log.error("Exception occurred inside executor", e);
        }
    }

    public void startBalanceChangeChecking() {
        checker.check()
                .onCompletion().invoke(logCompletion())
                .onTermination().invokeUni(reInvoke())
                .onFailure().invokeUni(printErrorAndReInvoke())
                .subscribe().with(item -> log.info("Updated user {}", item));
    }

    private BiFunction<Throwable, Boolean, Uni<?>> reInvoke() {
        return (t, b) -> Uni.createFrom().item(() -> {
            startBalanceChangeChecking();
            return null;
        });
    }

    private Function<Throwable, ? extends Uni<?>> printErrorAndReInvoke() {
        return (t) -> Uni.createFrom().item(() -> {
            log.error("Failed balance check process with error", t);
            startBalanceChangeChecking();
            return null;
        });
    }

    private Runnable logCompletion() {
        return () -> {
            var count = invocationCounter.incrementAndGet();
            if (count % PRINT_EVERY_COUNT == 0) {
                log.info("Completed balance check task #{}", count);
            }
        };
    }
}
