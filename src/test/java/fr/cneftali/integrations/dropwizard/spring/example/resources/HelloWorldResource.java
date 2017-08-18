package fr.cneftali.integrations.dropwizard.spring.example.resources;

import com.codahale.metrics.annotation.Timed;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON;

@Path("/hello-world")
@Produces(APPLICATION_JSON)
@Consumes(APPLICATION_JSON)
public interface HelloWorldResource {

    @GET
    @Timed
    Response get(@QueryParam("says") String says);

}