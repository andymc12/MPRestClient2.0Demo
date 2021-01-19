package io.openliberty.mprestclient.demo;

import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.RestClientBuilder;
import org.eclipse.microprofile.rest.client.ext.QueryParamStyle;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

@ApplicationPath("/demo")
@Path("/")
@Produces(MediaType.TEXT_PLAIN)
public class Resource extends Application {

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // QueryParamStyle
    @GET
    @Path("/queryParamStyle")
    public String queryParamStyle(@QueryParam("port") @DefaultValue("9080") int port) {
        StringBuilder sb = new StringBuilder();
        QueryParamClient client = RestClientBuilder.newBuilder()
                .baseUri(URI.create("http://localhost:" + port + "/query"))
                .register(PrintQueryParamsAndAbortFilter.class)
                .queryParamStyle(QueryParamStyle.MULTI_PAIRS) // default
                .build(QueryParamClient.class);

        sb.append("MULTI_PAIRS: ");
        sb.append(client.viewQueryString(Arrays.asList("a", "b", "c")));
        sb.append(System.lineSeparator());

        client = RestClientBuilder.newBuilder().baseUri(URI.create("http://localhost:" + port + "/query"))
                .register(PrintQueryParamsAndAbortFilter.class)
                .queryParamStyle(QueryParamStyle.COMMA_SEPARATED)
                .build(QueryParamClient.class);

        sb.append("COMMA_SEPARATED: ");
        sb.append(client.viewQueryString(Arrays.asList("a", "b", "c")));
        sb.append(System.lineSeparator());

        client = RestClientBuilder.newBuilder().baseUri(URI.create("http://localhost:" + port + "/query"))
                .register(PrintQueryParamsAndAbortFilter.class)
                .queryParamStyle(QueryParamStyle.ARRAY_PAIRS)
                .build(QueryParamClient.class);

        sb.append("ARRAY_PAIRS: ");
        sb.append(client.viewQueryString(Arrays.asList("a", "b", "c")));
        sb.append(System.lineSeparator()).append(System.lineSeparator());

        return sb.toString();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Follow Redirects
    @GET
    @Path("/redirect")
    public String followRedirect(@QueryParam("port") @DefaultValue("9080") int port) {
        StringBuilder sb = new StringBuilder();
        sb.append("DEFAULT CLIENT:").append(System.lineSeparator());
        RedirectClient client = RestClientBuilder.newBuilder()
                                                 .baseUri(URI.create("http://localhost:" + port + "/demo"))
                                                 .build(RedirectClient.class);
        sb.append(responseToString(client.aTaleOfTwoCities()));
        sb.append(System.lineSeparator()).append(System.lineSeparator());

        sb.append("AUTO-REDIRECTING CLIENT:").append(System.lineSeparator());
        client = RestClientBuilder.newBuilder()
                                  .baseUri(URI.create("http://localhost:" + port + "/demo"))
                                  .followRedirects(true)
                                  .build(RedirectClient.class);
        sb.append(responseToString(client.aTaleOfTwoCities()));
        sb.append(System.lineSeparator()).append(System.lineSeparator());

        return sb.toString();
    }

    private String responseToString(Response response) {
        StringBuilder sb = new StringBuilder();
        sb.append(response.getStatus()).append(" ");
        if (response.hasEntity())
            sb.append(response.readEntity(String.class)).append(" ");
        if (response.getHeaderString(HttpHeaders.LOCATION) != null)
            sb.append(HttpHeaders.LOCATION).append(": ").append(response.getHeaderString(HttpHeaders.LOCATION));

        return sb.toString();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // SSEs
    @GET
    @Path("/sse")
    public String sse(@QueryParam("port") @DefaultValue("9080") int port,
                      @QueryParam("numEvents") @DefaultValue("10") int numEvents,
                      @QueryParam("cancelAfter") @DefaultValue("10") int cancelAfter) throws Exception {
        StringBuilder sb = new StringBuilder();
        CountDownLatch latch = new CountDownLatch(1);
        try (SseClient client = RestClientBuilder.newBuilder()
                                                 .baseUri(URI.create("http://localhost:" + port + "/demo"))
                                                 .build(SseClient.class)) {


            client.getEvents(numEvents).subscribe(new Subscriber<String>() {

                int count = 0;
                Subscription subscription;
                @Override
                public void onSubscribe(Subscription s) {
                    subscription = s;
                    sb.append("Subscribed").append(System.lineSeparator());
                    s.request(cancelAfter);
                }

                @Override
                public void onNext(String s) {
                    sb.append("Received Event: ").append(s).append(System.lineSeparator());
                    if (++count >= cancelAfter) {
                        sb.append("Canceling after receiving ").append(cancelAfter).append(" events");
                        subscription.cancel();
                        latch.countDown();
                    }
                }

                @Override
                public void onError(Throwable t) {
                    sb.append("Received Error: ").append(t);
                }

                @Override
                public void onComplete() {
                    sb.append("Connection closed by remote server");
                    latch.countDown();
                }

            });
        }
        latch.await(45, TimeUnit.SECONDS);
        sb.append(System.lineSeparator());
        return sb.toString();
    }
}
