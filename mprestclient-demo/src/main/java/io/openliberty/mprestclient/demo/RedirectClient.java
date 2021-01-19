package io.openliberty.mprestclient.demo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/old")
@Produces(MediaType.TEXT_PLAIN)
public interface RedirectClient {

    @GET
    Response aTaleOfTwoCities();
}
