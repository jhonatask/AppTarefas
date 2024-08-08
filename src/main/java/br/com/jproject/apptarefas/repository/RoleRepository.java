package br.com.jproject.apptarefas.repository;

import br.com.jproject.apptarefas.entities.Role;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface RoleRepository extends ReactiveMongoRepository<Role, Long> {
    Mono<Role> findByName(String name);
}
