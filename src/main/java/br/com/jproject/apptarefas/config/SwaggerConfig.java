package br.com.jproject.apptarefas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;

@Configuration
public class SwaggerConfig {

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.OAS_30)
                .select()
                .apis(RequestHandlerSelectors.basePackage("br.com.jproject.apptarefas"))
                .paths(PathSelectors.any())
                .build();
    }

    private ApiInfo metaInfo() {

        return new ApiInfo(
                "AppTares API REST",
                "API REST de gerenciamento de tarefas.",
                "1.0",
                "Terms of Service",
                new Contact("Jhonata Nazareno", "https://www.linkedin.com/in/jhonatask/",
                        "jhonatask@gmail.com"),
                "Apache License Version 2.0",
                "https://www.apache.org/licesen.html", new ArrayList<>()
        );
    }
}
