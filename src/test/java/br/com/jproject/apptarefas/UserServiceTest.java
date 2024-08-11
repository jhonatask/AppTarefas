package br.com.jproject.apptarefas;

import br.com.jproject.apptarefas.core.exceptions.exception.UserNotFoundException;
import br.com.jproject.apptarefas.dto.request.UserRequestDTO;
import br.com.jproject.apptarefas.dto.response.UserResponseDTO;
import br.com.jproject.apptarefas.entities.Role;
import br.com.jproject.apptarefas.entities.User;
import br.com.jproject.apptarefas.mapper.UserResponseMapperDTO;
import br.com.jproject.apptarefas.repository.RoleRepository;
import br.com.jproject.apptarefas.repository.UserRepository;
import br.com.jproject.apptarefas.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private UserResponseMapperDTO userResponseMapperDTO;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void createUser_withUniqueEmail() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setEmail("unique@example.com");
        User user = new User();
        user.setId(UUID.randomUUID());
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());
        when(roleRepository.findByName(anyString())).thenReturn(Mono.just(new Role(1L, "ADMIN")));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));
        when(userResponseMapperDTO.userToUserResponseDTO(any(User.class))).thenReturn(new UserResponseDTO());
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));
        when(userResponseMapperDTO.userToUserResponseDTO(any(User.class))).thenReturn(new UserResponseDTO());

        StepVerifier.create(userService.createUser(userRequestDTO))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void createUser_withDuplicateEmail() {
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        userRequestDTO.setEmail("duplicate@example.com");
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(new User()));

        StepVerifier.create(userService.createUser(userRequestDTO))
                .expectErrorMatches(throwable -> throwable instanceof Exception && throwable.getMessage().equals("Não é possível cadastrar usuário com mesmo email"))
                .verify();
    }

    @Test
    public void getAllUsers_returnsUsers() {
        User user = new User();
        when(userRepository.findAll()).thenReturn(Flux.just(user));
        when(userResponseMapperDTO.userToUserResponseDTO(any(User.class))).thenReturn(new UserResponseDTO());

        StepVerifier.create(userService.getAllUsers())
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void getUserById_existingId() {
        UUID id = UUID.randomUUID();
        User user = new User();
        when(userRepository.findById(id)).thenReturn(Mono.just(user));
        when(userResponseMapperDTO.userToUserResponseDTO(any(User.class))).thenReturn(new UserResponseDTO());

        StepVerifier.create(userService.getUserById(id))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void getUserById_nonExistingId() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(userService.getUserById(id))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    public void updateUser_existingId() {
        UUID id = UUID.randomUUID();
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        User user = new User();
        when(userRepository.findById(id)).thenReturn(Mono.just(user));
        when(userRepository.save(any(User.class))).thenReturn(Mono.just(user));
        when(userResponseMapperDTO.userToUserResponseDTO(any(User.class))).thenReturn(new UserResponseDTO());

        StepVerifier.create(userService.updateUser(id, userRequestDTO))
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void updateUser_nonExistingId() {
        UUID id = UUID.randomUUID();
        UserRequestDTO userRequestDTO = new UserRequestDTO();
        when(userRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(userService.updateUser(id, userRequestDTO))
                .expectError(UserNotFoundException.class)
                .verify();
    }

    @Test
    public void deleteUser_existingId() {
        UUID id = UUID.randomUUID();
        User user = new User();
        when(userRepository.findById(id)).thenReturn(Mono.just(user));
        when(userRepository.delete(user)).thenReturn(Mono.empty());

        StepVerifier.create(userService.deleteUser(id))
                .verifyComplete();
    }

    @Test
    public void deleteUser_nonExistingId() {
        UUID id = UUID.randomUUID();
        when(userRepository.findById(id)).thenReturn(Mono.empty());

        StepVerifier.create(userService.deleteUser(id))
                .expectError(UserNotFoundException.class)
                .verify();
    }
}
