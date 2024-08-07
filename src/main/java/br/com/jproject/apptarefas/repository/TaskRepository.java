package br.com.jproject.apptarefas.repository;

import br.com.jproject.apptarefas.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.UUID;


@Repository
public interface TaskRepository extends ReactiveMongoRepository<Task, UUID> {
    Mono<Page<Task>> findAll(Pageable pageable);
    Mono<Task> findById(UUID id);
    Mono<Page<Task>> findByStatusContaining(String status, Pageable pageable);
}

