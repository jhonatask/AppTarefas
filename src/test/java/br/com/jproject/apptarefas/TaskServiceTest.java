package br.com.jproject.apptarefas;

import br.com.jproject.apptarefas.dto.request.TaskRequestDTO;
import br.com.jproject.apptarefas.dto.response.TaskDTO;
import br.com.jproject.apptarefas.entities.Task;
import br.com.jproject.apptarefas.enums.StatusTask;
import br.com.jproject.apptarefas.mapper.TaskMapperDTO;
import br.com.jproject.apptarefas.repository.TaskRepository;
import br.com.jproject.apptarefas.services.S3Service;
import br.com.jproject.apptarefas.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapperDTO taskMapperDTO;

    @Mock
    private S3Service s3Service;

    @InjectMocks
    private TaskService taskService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void findTaskAllOrFilter_withStatus() {
        Task task = new Task();
        task.setStatus(StatusTask.OPEN.getStatus());
        when(taskRepository.findByStatusContaining(anyString())).thenReturn(Flux.just(task));
        when(taskMapperDTO.taskTotaskDTO(any(Task.class))).thenReturn(new TaskDTO());

        StepVerifier.create(taskService.findTaskAllOrFilter("OPEN"))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void findTaskAllOrFilter_withoutStatus() {
        Task task = new Task();
        when(taskRepository.findAll()).thenReturn(Flux.just(task));
        when(taskMapperDTO.taskTotaskDTO(any(Task.class))).thenReturn(new TaskDTO());

        StepVerifier.create(taskService.findTaskAllOrFilter(null))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void createTask_withFile() {
        TaskRequestDTO taskRequestDTO = new TaskRequestDTO();
        MultipartFile file = null; // Mock file as needed
        Task task = new Task();
        task.setId(UUID.randomUUID());
        when(taskMapperDTO.taskRequestDtoTotask(any(TaskRequestDTO.class))).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(task));
        when(s3Service.uploadFile(anyString(), any(MultipartFile.class))).thenReturn(Mono.just("url"));
        when(taskMapperDTO.taskTotaskDTO(any(Task.class))).thenReturn(new TaskDTO());

        StepVerifier.create(taskService.createTask(taskRequestDTO, file))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void createTask_withoutFile() {
        TaskRequestDTO taskRequestDTO = new TaskRequestDTO();
        MultipartFile file = null;
        Task task = new Task();
        when(taskMapperDTO.taskRequestDtoTotask(any(TaskRequestDTO.class))).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(task));
        when(taskMapperDTO.taskTotaskDTO(any(Task.class))).thenReturn(new TaskDTO());

        StepVerifier.create(taskService.createTask(taskRequestDTO, file))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void deleteTask_existingId() {
        UUID id = UUID.randomUUID();
        Task task = new Task();
        when(taskRepository.findById(id)).thenReturn(Mono.just(task));
        when(taskRepository.delete(task)).thenReturn(Mono.empty());

        StepVerifier.create(taskService.deleteTask(id))
                .verifyComplete();
    }

    @Test
    public void updateTask_existingId() {
        UUID id = UUID.randomUUID();
        TaskRequestDTO taskRequestDTO = new TaskRequestDTO();
        Task task = new Task();
        when(taskRepository.findById(id)).thenReturn(Mono.just(task));
        when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(task));
        when(taskMapperDTO.taskTotaskDTO(any(Task.class))).thenReturn(new TaskDTO());

        StepVerifier.create(taskService.updateTask(id, taskRequestDTO))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void makeTaskConcluded_existingId() {
        UUID id = UUID.randomUUID();
        Task task = new Task();
        when(taskRepository.findById(id)).thenReturn(Mono.just(task));
        when(taskRepository.save(any(Task.class))).thenReturn(Mono.just(task));
        when(taskMapperDTO.taskTotaskDTO(any(Task.class))).thenReturn(new TaskDTO());

        StepVerifier.create(taskService.makeTaskConcluded(id))
                .expectNextCount(1)
                .verifyComplete();
    }
}
