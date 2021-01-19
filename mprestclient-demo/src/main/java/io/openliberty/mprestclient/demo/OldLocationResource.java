package io.openliberty.mprestclient.demo;

import java.net.URI;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("/old")
@Produces(MediaType.TEXT_PLAIN)
public class OldLocationResource {

    @GET
    public Response aTaleOfTwoCities() {
        System.out.println("old");
        return Response.status(Status.TEMPORARY_REDIRECT)
                       .location(URI.create("http://localhost:9080/demo/new"))
                       .build();
    }
}
