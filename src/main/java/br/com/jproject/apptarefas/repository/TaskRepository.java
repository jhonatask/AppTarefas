package br.com.jproject.apptarefas.repository;

import br.com.jproject.apptarefas.entities.Task;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Repository
public interface TaskRepository extends ReactiveMongoRepository<Task, UUID> {
    Flux<Task> findAll();
    Mono<Task> findById(UUID id);
    Flux<Task> findByStatusContaining(String status);
}

