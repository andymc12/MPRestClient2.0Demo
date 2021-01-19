package io.openliberty.mprestclient.demo;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

public interface QueryParamClient {

    @Path("/query")
    @GET
    String viewQueryString(@QueryParam("query") List<String> queryValues);
}
