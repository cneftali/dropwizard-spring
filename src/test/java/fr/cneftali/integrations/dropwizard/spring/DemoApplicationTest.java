package fr.cneftali.integrations.dropwizard.spring;

import fr.cneftali.integrations.dropwizard.spring.example.DemoApplication;
import fr.cneftali.integrations.dropwizard.spring.example.DemoConfiguration;
import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import org.eclipse.jetty.http.HttpStatus;
import org.junit.ClassRule;
import org.junit.Test;

import javax.ws.rs.core.Response;

import static javax.ws.rs.core.MediaType.APPLICATION_JSON_TYPE;
import static org.assertj.core.api.Assertions.assertThat;

public class DemoApplicationTest {

    private static final String CONFIG_PATH = ResourceHelpers.resourceFilePath("test-default-config.yml");


    @ClassRule
    public static final DropwizardAppRule<DemoConfiguration> DROPWIZARD = new DropwizardAppRule<>(DemoApplication.class,
                                                                                                  CONFIG_PATH);


    @Test
    public void healthCheck() throws Exception {
        // Given

        // When
        final Response response = this.DROPWIZARD.client()
                                                 .target("http://localhost:" + DROPWIZARD.getAdminPort())
                                                 .path("healthcheck")
                                                 .queryParam("pretty",
                                                             true)
                                                 .request(APPLICATION_JSON_TYPE)
                                                 .get();

        // Then
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(HttpStatus.OK_200);
    }
}