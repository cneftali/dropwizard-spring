package fr.cneftali.integrations.dropwizard.spring.example.resources;

import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.core.Response;

@Slf4j
public class HelloWorldResourceImpl implements HelloWorldResource {
    @Override
    public Response get(final String says) {
        log.info(says);
        return Response.ok(says)
                       .build();
    }
}
