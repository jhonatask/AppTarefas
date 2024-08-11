package br.com.jproject.apptarefas;

import br.com.jproject.apptarefas.dto.request.UserRequestDTO;
import br.com.jproject.apptarefas.dto.response.UserResponseDTO;
import br.com.jproject.apptarefas.rest.UserController;
import br.com.jproject.apptarefas.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class UserControllerTest {
    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUser_ReturnsCreatedUser_WhenValidRequest() throws Exception {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        when(userService.createUser(any(UserRequestDTO.class))).thenReturn(Mono.just(userResponseDTO));

        Mono<ResponseEntity<UserResponseDTO>> response = userController.createUser(userRequestDTO);

        assertEquals(HttpStatus.CREATED, response.block().getStatusCode());
    }

    @Test
    void getAllUsers_ReturnsListOfUsers() {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        when(userService.getAllUsers()).thenReturn(Flux.just(userResponseDTO));

        ResponseEntity<Flux<UserResponseDTO>> response = userController.getAllUsers();

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void getUserById_ReturnsUser_WhenUserExists() {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        UUID id = UUID.randomUUID();
        when(userService.getUserById(any(UUID.class))).thenReturn(Mono.just(userResponseDTO));

        Mono<ResponseEntity<UserResponseDTO>> response = userController.getUserById(id);

        assertEquals(HttpStatus.OK, response.block().getStatusCode());
    }

    @Test
    void getUserById_ReturnsError_WhenUserDoesNotExist() {
        UUID id = UUID.randomUUID();
        when(userService.getUserById(any(UUID.class))).thenReturn(Mono.empty());

        Mono<ResponseEntity<UserResponseDTO>> response = userController.getUserById(id);

        assertEquals(HttpStatus.NOT_FOUND, response.block().getStatusCode());
    }

    @Test
    void updateUser_ReturnsUpdatedUser_WhenUserExists() {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        UUID id = UUID.randomUUID();
        when(userService.updateUser(any(UUID.class), any(UserRequestDTO.class))).thenReturn(Mono.just(userResponseDTO));

        Mono<ResponseEntity<UserResponseDTO>> response = userController.updateUser(id, userRequestDTO);

        assertEquals(HttpStatus.OK, response.block().getStatusCode());
    }

    @Test
    void updateUser_ReturnsError_WhenUserDoesNotExist() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        UUID id = UUID.randomUUID();
        when(userService.updateUser(any(UUID.class), any(UserRequestDTO.class))).thenReturn(Mono.empty());

        Mono<ResponseEntity<UserResponseDTO>> response = userController.updateUser(id, userRequestDTO);

        assertEquals(HttpStatus.NOT_FOUND, response.block().getStatusCode());
    }

    @Test
    void deleteUser_ReturnsNoContent_WhenUserExists() {
        UUID id = UUID.randomUUID();
        when(userService.deleteUser(any(UUID.class))).thenReturn(Mono.empty());

        ResponseEntity<Void> response = userController.deleteUser(id);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }
}
