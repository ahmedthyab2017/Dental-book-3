package com.dantal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class DantalApplication {

    public static void main(String[] args) {
        SpringApplication.run(DantalApplication.class, args);
    }
}
