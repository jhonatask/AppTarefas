package br.com.jproject.apptarefas.repository;

import br.com.jproject.apptarefas.entities.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface UserRepository extends ReactiveMongoRepository<User, UUID> {
    Mono<User> findByEmail(String email);
    Mono<User> findByName(String name);
}
