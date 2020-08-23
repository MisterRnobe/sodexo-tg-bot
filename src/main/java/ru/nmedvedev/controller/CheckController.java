package ru.nmedvedev.controller;


import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/api/1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class CheckController {


    @GET
    @Path("/cards/{card}")
    public Map stub(@PathParam(value = "card") String card) {
        return Map.of();
    }


}
