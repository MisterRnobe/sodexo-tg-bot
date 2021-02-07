package ru.nmedvedev.controller;

import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.annotations.jaxrs.QueryParam;
import ru.nmedvedev.service.spendmoneyreminder.SpendMoneyReminderService;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.time.LocalDate;

/**
 * This one is for testing purposes only.
 */
@Path("/api/spend-money-reminder")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@RequiredArgsConstructor
public class SpendingMoneyReminderController {

    private final SpendMoneyReminderService spendMoneyReminderService;

    @GET
    public void tryForParticularDate(@QueryParam("date") String localDate) {
        spendMoneyReminderService.sendRemindersForDate(LocalDate.parse(localDate));
    }

}
