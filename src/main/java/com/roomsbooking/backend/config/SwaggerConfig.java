package com.roomsbooking.backend.config;

import io.swagger.annotations.Api;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger.web.SwaggerResource;
import springfox.documentation.swagger.web.SwaggerResourcesProvider;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Class responsible for configuring swagger documentation.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    @Value("${swagger.source.filepath}")
    private String swaggerSourceFilepath;

    /**
     * Method responsible for providing swagger resources.
     *
     * @return SwaggerResourceProvider object
     */
    @Primary
    @Bean
    public SwaggerResourcesProvider swaggerResourcesProvider() {
        return () -> {
            SwaggerResource swaggerResource = new SwaggerResource();
            swaggerResource.setName("Documentation");
            swaggerResource.setSwaggerVersion("3.0");
            swaggerResource.setLocation(swaggerSourceFilepath);
            return List.of(swaggerResource);
        };
    }

    /**
     * Method responsible for configuring Docket.
     *
     * @return Docket object
     */
    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
            .useDefaultResponseMessages(false)
            .select()
            .apis(RequestHandlerSelectors.withClassAnnotation(Api.class))
            .paths(PathSelectors.any())
            .build();
    }
}
