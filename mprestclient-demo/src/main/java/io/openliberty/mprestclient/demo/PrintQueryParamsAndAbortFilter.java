package io.openliberty.mprestclient.demo;

import java.io.IOException;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public class PrintQueryParamsAndAbortFilter implements ClientRequestFilter {

    @Override
    public void filter(ClientRequestContext crc) throws IOException {
        String query = crc.getUri().getQuery();
        if (query != null) {
            crc.abortWith(Response.ok(query).type(MediaType.TEXT_PLAIN).build());
        }
    }
    
}
