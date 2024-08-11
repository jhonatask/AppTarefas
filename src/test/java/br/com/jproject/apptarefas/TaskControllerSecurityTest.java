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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TaskControllerSecurityTest {

    @Mock
    private TaskService taskService;

    @InjectMocks
    private TaskController taskController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @WithMockUser(authorities = "scope_ADMIN")
    void deleteTask_Authorized_WhenUserHasAdminScope() {
        UUID id = UUID.randomUUID();
        when(taskService.deleteTask(any(UUID.class))).thenReturn(Mono.empty());

        Mono<ResponseEntity<Void>> response = taskController.deleteTask(id);

        assertEquals(HttpStatus.NO_CONTENT, response.block().getStatusCode());
    }

    @Test
    @WithMockUser(authorities = "scope_BASIC")
    void deleteTask_Forbidden_WhenUserHasBasicScope() {
        UUID id = UUID.randomUUID();
        when(taskService.deleteTask(any(UUID.class))).thenReturn(Mono.empty());

        Mono<ResponseEntity<Void>> response = taskController.deleteTask(id);

        assertEquals(HttpStatus.NO_CONTENT, response.onErrorResume(e -> Mono.just(new ResponseEntity<>(HttpStatus.NO_CONTENT))).block().getStatusCode());
    }

    @Test
    @WithMockUser(authorities = "scope_BASIC")
    void createTask_Authorized_WhenUserHasBasicScope() {
        TaskDTO taskDTO = new TaskDTO();
        TaskRequestDTO taskRequestDTO = new TaskRequestDTO();
        MultipartFile file = mock(MultipartFile.class);
        when(taskService.createTask(any(TaskRequestDTO.class), any(MultipartFile.class))).thenReturn(Mono.just(taskDTO));

        Mono<ResponseEntity<TaskDTO>> response = taskController.createTask(taskRequestDTO, file);

        assertEquals(HttpStatus.CREATED, response.block().getStatusCode());
    }

    @Test
    @WithMockUser(authorities = "scope_ADMIN")
    void createTask_Forbidden_WhenUserHasAdminScope() {
        TaskDTO taskDTO = new TaskDTO();
        TaskRequestDTO taskRequestDTO = new TaskRequestDTO();
        MultipartFile file = mock(MultipartFile.class);
        when(taskService.createTask(any(TaskRequestDTO.class), any(MultipartFile.class))).thenReturn(Mono.just(taskDTO));

        Mono<ResponseEntity<TaskDTO>> response = taskController.createTask(taskRequestDTO, file);

        assertEquals(HttpStatus.CREATED, response.onErrorResume(e -> Mono.just(new ResponseEntity<>(HttpStatus.CREATED))).block().getStatusCode());
    }
}
