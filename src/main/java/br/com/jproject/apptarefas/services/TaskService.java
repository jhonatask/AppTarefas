package br.com.jproject.apptarefas.services;

import br.com.jproject.apptarefas.dto.request.TaskRequestDTO;
import br.com.jproject.apptarefas.dto.response.TaskDTO;
import br.com.jproject.apptarefas.entities.Task;
import br.com.jproject.apptarefas.enums.StatusTask;
import br.com.jproject.apptarefas.mapper.TaskMapperDTO;
import br.com.jproject.apptarefas.repository.TaskRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class TaskService {


    private final TaskRepository taskRepository;
    private final TaskMapperDTO taskMapperDTO;

    public TaskService(TaskRepository taskRepository, TaskMapperDTO taskMapperDTO) {
        this.taskRepository = taskRepository;
        this.taskMapperDTO = taskMapperDTO;
    }

    public Flux<TaskDTO> findTaskAllOrFilter(String status, Pageable pageable) {
        if (status == null) {
            return taskRepository.findAll()
                    .buffer(pageable.getPageSize(), pageable.getPageNumber() * pageable.getPageSize())
                    .flatMap(Flux::fromIterable)
                    .map(taskMapperDTO::taskTotaskDTO);
        } else {
            return taskRepository.findByStatusContaining(status, pageable)
                    .buffer(pageable.getPageSize(), pageable.getPageNumber() * pageable.getPageSize())
                    .flatMap(Flux::fromIterable)
                    .map(taskMapperDTO::taskTotaskDTO);
        }
    }

    public Mono<TaskDTO> createTask(TaskRequestDTO taskDTO) {
        Task task = taskMapperDTO.taskRequestDtoTotask(taskDTO);
        task.setName(taskDTO.getName());
        task.setDescription(taskDTO.getDescription());
        task.setPriority(taskDTO.getPriority());
        task.setStatus(StatusTask.OPEN.getStatus());
        return taskRepository.save(task)
                .map(taskMapperDTO::taskTotaskDTO);
    }

    public Mono<Void> deleteTask(UUID id) {
        return taskRepository.findById(id)
                .flatMap(taskRepository::delete);
    }

    public Mono<TaskDTO> updateTask(UUID id, TaskRequestDTO taskDTO) {
        return taskRepository.findById(id)
                .flatMap(task -> {
                    task.setPriority(taskDTO.getPriority());
                    task.setDescription(taskDTO.getDescription());
                    return taskRepository.save(task);
                })
                .map(taskMapperDTO::taskTotaskDTO);
    }

    public Mono<TaskDTO> makeTaskConcluded(UUID id) {
        return taskRepository.findById(id)
                .flatMap(task -> {
                    task.setStatus(StatusTask.DONE.getStatus());
                    return taskRepository.save(task);
                })
                .map(taskMapperDTO::taskTotaskDTO);
    }
}
