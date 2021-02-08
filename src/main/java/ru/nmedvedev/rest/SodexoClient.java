package ru.nmedvedev.rest;

import io.smallrye.mutiny.Uni;
import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import ru.nmedvedev.model.SodexoResponse;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;


@Path("/api/1")
@RegisterRestClient
public interface SodexoClient {

    @GET
    @Path("/cards/{card}")
    @Produces("application/json")
    @ClientHeaderParam(name = "X-Requested-With", value = "XMLHttpRequest")
    Uni<SodexoResponse> getByCard(@PathParam(value = "card") String card);

}
