package ru.nmedvedev.controller;


import org.jboss.resteasy.annotations.jaxrs.PathParam;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Path("/api/1")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class StubController {

    private Map stub = Map.of();

    @GET
    @Path("/cards/{card}")
    public Map get(@PathParam(value = "card") String card) {
        return stub;
    }

    @POST
    @Path("/cards/{card}")
    public void post(Map body) {
        this.stub = body;
    }

}
