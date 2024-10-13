package io.hhplus.swagger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SwaggerApplication {

    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(SwaggerApplication.class);
        app.setAdditionalProfiles("swagger");
        app.run(args);
    }

}
