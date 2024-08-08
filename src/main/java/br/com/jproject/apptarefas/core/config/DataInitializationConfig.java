package br.com.jproject.apptarefas.core.config;

import br.com.jproject.apptarefas.entities.Role;
import br.com.jproject.apptarefas.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import java.util.Arrays;

@Configuration
@Profile("!test")
public class DataInitializationConfig {


    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private ReactiveMongoTemplate reactiveMongoTemplate;

    @PostConstruct
    public void init() {
        reactiveMongoTemplate.collectionExists(Role.class)
                .flatMap(exists -> exists ? reactiveMongoTemplate.dropCollection(Role.class) : reactiveMongoTemplate.createCollection(Role.class))
                .thenMany(roleRepository.saveAll(Arrays.asList(
                        new Role(1L, "ADMIN"),
                        new Role(2L, "BASIC")
                )))
                .subscribe();
    }
}
