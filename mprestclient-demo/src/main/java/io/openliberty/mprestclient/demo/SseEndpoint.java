package io.openliberty.mprestclient.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.sse.Sse;
import javax.ws.rs.sse.SseEventSink;

@Path("/sseEndpoint")
@Produces(MediaType.SERVER_SENT_EVENTS)
public class SseEndpoint {

    ExecutorService executor = Executors.newFixedThreadPool(4);

    @GET
    @Path("/{numEvents}")
    public void sendStringEvents(@Context Sse sse, @Context SseEventSink sink, @PathParam("numEvents") int numEvents) {
        //System.out.println("sendStringEvents invoked");
        executor.submit(() -> {
            try (SseEventSink sseSink = sink) {
                for (int i=0; i<numEvents; i++) {
                    sink.send(sse.newEvent("" + (char)('a' + i)));
                    try {Thread.sleep(500); } catch (InterruptedException ex) { }
                }
            }
        });
    }
}
