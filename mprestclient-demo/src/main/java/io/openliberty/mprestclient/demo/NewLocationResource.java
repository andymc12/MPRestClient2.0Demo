package io.openliberty.mprestclient.demo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/new")
@Produces(MediaType.TEXT_PLAIN)
public class NewLocationResource {

    @GET
    public Response aTaleOfTwoCities() {
        System.out.println("new");
        return Response.ok("It was the best of times, it was the worst of times...", MediaType.TEXT_PLAIN).build();
    }
}
