package fr.cneftali.integrations.dropwizard.spring.example;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
public class DemoConfiguration extends Configuration {

    @NotNull
    @JsonProperty("myConfigurationElement")
    private String myConfigurationElement = "default value";
}