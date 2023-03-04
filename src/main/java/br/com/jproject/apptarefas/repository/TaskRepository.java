package br.com.jproject.apptarefas.repository;

import br.com.jproject.apptarefas.entity.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    Page<Task> findByStatusContaining(String status, Pageable pageable);
}

