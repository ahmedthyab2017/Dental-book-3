package com.dantal.config.properties;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Getter
@Setter
@Validated
@ConfigurationProperties(prefix = "dantal.security.cors")
public class CorsProperties {

    @NotEmpty
    private List<String> allowedOrigins;

    @NotEmpty
    private List<String> allowedMethods;

    @NotEmpty
    private List<String> allowedHeaders;

    private long maxAgeSeconds = 3600;
}
