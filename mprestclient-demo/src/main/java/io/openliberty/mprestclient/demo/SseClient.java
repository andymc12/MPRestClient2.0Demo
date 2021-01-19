package io.openliberty.mprestclient.demo;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.reactivestreams.Publisher;

@Path("/sseEndpoint")
public interface SseClient extends AutoCloseable {

    @GET
    @Path("/{numEvents}")
    @Produces(MediaType.SERVER_SENT_EVENTS)
    Publisher<String> getEvents(@PathParam("numEvents") int numEvents);
}
