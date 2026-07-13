package com.dantal.platform.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "dantal.seed")
public class ProdSuperAdminSeedProperties {

    private boolean superAdminEnabled = false;
    private String email = "";
    private String password = "";
}
