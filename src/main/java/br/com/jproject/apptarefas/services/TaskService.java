package br.com.jproject.apptarefas.services;

import br.com.jproject.apptarefas.dto.TaskDTO;
import br.com.jproject.apptarefas.entity.Task;
import br.com.jproject.apptarefas.enums.StatusTask;
import br.com.jproject.apptarefas.mapper.TaskMapperDTO;
import br.com.jproject.apptarefas.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
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

    public Mono<Page<TaskDTO>> findTaskAllOrFilter(String status, Pageable pageable) {
        return (status == null ? taskRepository.findAll(pageable) : taskRepository.findByStatusContaining(status, pageable))
                .map(page -> page.map(taskMapperDTO::taskTotaskDTO));
    }

    public Mono<TaskDTO> createTask(TaskDTO taskDTO) {
        Task task = taskMapperDTO.taskDtoToTask(taskDTO);
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

    public Mono<TaskDTO> updateTask(UUID id, TaskDTO taskDTO) {
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
