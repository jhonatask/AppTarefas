package br.com.jproject.apptarefas;

import br.com.jproject.apptarefas.dto.request.TaskRequestDTO;
import br.com.jproject.apptarefas.dto.response.TaskDTO;
import br.com.jproject.apptarefas.rest.TaskController;
import br.com.jproject.apptarefas.services.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TaskControllerTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void findTask_ReturnsTasks_WhenStatusIsProvided() {
        TaskDTO taskDTO = new TaskDTO();
        when(taskService.findTaskAllOrFilter(anyString())).thenReturn(Flux.just(taskDTO));

        Flux<ResponseEntity<TaskDTO>> response = taskController.findTask("Open");

        assertEquals(HttpStatus.OK, response.blockFirst().getStatusCode());
    }

    @Test
    void findTask_ReturnsTasks_WhenStatusIsNotProvided() {
        TaskDTO taskDTO = new TaskDTO();
        when(taskService.findTaskAllOrFilter(null)).thenReturn(Flux.just(taskDTO));

        Flux<ResponseEntity<TaskDTO>> response = taskController.findTask(null);

        assertEquals(HttpStatus.OK, response.blockFirst().getStatusCode());
    }

    @Test
    void createTask_ReturnsCreatedTask_WhenValidRequest() {
        TaskDTO taskDTO = new TaskDTO();
        TaskRequestDTO taskRequestDTO = new TaskRequestDTO();
        MultipartFile file = mock(MultipartFile.class);
        when(taskService.createTask(any(TaskRequestDTO.class), any(MultipartFile.class))).thenReturn(Mono.just(taskDTO));

        Mono<ResponseEntity<TaskDTO>> response = taskController.createTask(taskRequestDTO, file);

        assertEquals(HttpStatus.CREATED, response.block().getStatusCode());
    }

    @Test
    void updateTask_ReturnsUpdatedTask_WhenTaskExists() {
        TaskDTO taskDTO = new TaskDTO();
        TaskRequestDTO taskRequestDTO = new TaskRequestDTO();
        UUID id = UUID.randomUUID();
        when(taskService.updateTask(any(UUID.class), any(TaskRequestDTO.class))).thenReturn(Mono.just(taskDTO));

        Mono<ResponseEntity<TaskDTO>> response = taskController.updateTask(id, taskRequestDTO);

        assertEquals(HttpStatus.OK, response.block().getStatusCode());
    }

    @Test
    void updateTask_ReturnsError_WhenTaskDoesNotExist() {
        TaskRequestDTO taskRequestDTO = new TaskRequestDTO();
        UUID id = UUID.randomUUID();
        when(taskService.updateTask(any(UUID.class), any(TaskRequestDTO.class))).thenReturn(Mono.empty());

        Mono<ResponseEntity<TaskDTO>> response = taskController.updateTask(id, taskRequestDTO);

        assertEquals(null, response.onErrorResume(e -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND))).block().getBody());
    }

    @Test
    void makeTaskConcluded_ReturnsUpdatedTask_WhenTaskExists() {
        TaskDTO taskDTO = new TaskDTO();
        UUID id = UUID.randomUUID();
        when(taskService.makeTaskConcluded(any(UUID.class))).thenReturn(Mono.just(taskDTO));

        Mono<ResponseEntity<TaskDTO>> response = taskController.makeTaskConcluded(id);

        assertEquals(HttpStatus.OK, response.block().getStatusCode());
    }

    @Test
    void makeTaskConcluded_ReturnsError_WhenTaskDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(taskService.makeTaskConcluded(any(UUID.class))).thenReturn(Mono.empty());

        Mono<ResponseEntity<TaskDTO>> response = taskController.makeTaskConcluded(id);

        assertEquals(null, response.onErrorResume(e -> Mono.just(new ResponseEntity<>(HttpStatus.NOT_FOUND))).block().getBody());
    }

    @Test
    void deleteTask_ReturnsNoContent_WhenTaskExists() {
        UUID id = UUID.randomUUID();
        when(taskService.deleteTask(any(UUID.class))).thenReturn(Mono.empty());

        Mono<ResponseEntity<Void>> response = taskController.deleteTask(id);

        assertEquals(HttpStatus.NO_CONTENT, response.block().getStatusCode());
    }
}
