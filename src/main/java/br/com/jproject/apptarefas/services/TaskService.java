package br.com.jproject.apptarefas.services;

import br.com.jproject.apptarefas.dto.request.TaskRequestDTO;
import br.com.jproject.apptarefas.dto.response.TaskDTO;
import br.com.jproject.apptarefas.entities.Task;
import br.com.jproject.apptarefas.enums.StatusTask;
import br.com.jproject.apptarefas.mapper.TaskMapperDTO;
import br.com.jproject.apptarefas.repository.TaskRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class TaskService {


    private final TaskRepository taskRepository;
    private final TaskMapperDTO taskMapperDTO;
    private final S3Service s3Service;

    public TaskService(TaskRepository taskRepository, TaskMapperDTO taskMapperDTO, S3Service s3Service) {
        this.taskRepository = taskRepository;
        this.taskMapperDTO = taskMapperDTO;
        this.s3Service = s3Service;
    }

    public Flux<TaskDTO> findTaskAllOrFilter(String status) {

        if (status == null) {
            return taskRepository.findAll()
                    .map(taskMapperDTO::taskTotaskDTO);
        } else {
            return taskRepository.findByStatusContaining(status)
                    .map(taskMapperDTO::taskTotaskDTO);
        }
    }

    public Mono<TaskDTO> createTask(TaskRequestDTO taskDTO, MultipartFile file) {
        Task task = taskMapperDTO.taskRequestDtoTotask(taskDTO);
        task.setName(taskDTO.getName());
        task.setDescription(taskDTO.getDescription());
        task.setPriority(taskDTO.getPriority());
        task.setStatus(StatusTask.OPEN.getStatus());

        return taskRepository.save(task)
                .flatMap(savedTask -> {
                    if (file != null && !file.isEmpty()) {
                        String key = "task-files/" + savedTask.getId() + "/" + file.getOriginalFilename();
                        return s3Service.uploadFile(key, file)
                                .flatMap(url -> {
                                    savedTask.setFileUrl(url);
                                    return taskRepository.save(savedTask);
                                })
                                .then(Mono.just(taskMapperDTO.taskTotaskDTO(savedTask)));
                    } else {
                        return Mono.just(taskMapperDTO.taskTotaskDTO(savedTask));
                    }
                });
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
