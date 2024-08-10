package br.com.jproject.apptarefas;

import br.com.jproject.apptarefas.dto.request.LoginRequestDTO;
import br.com.jproject.apptarefas.dto.response.ResponseTokenDTO;
import br.com.jproject.apptarefas.entities.Role;
import br.com.jproject.apptarefas.entities.User;
import br.com.jproject.apptarefas.repository.UserRepository;
import br.com.jproject.apptarefas.rest.AuthController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.Collections;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class AuthControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtEncoder jwtEncoder;

    @Mock
    private ReactiveRedisTemplate<String, Object> redisTemplate;

    @Mock
    private ReactiveValueOperations<String, Object> valueOperations;

    @InjectMocks
    private AuthController authController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    @Test
    void loginSuccessful() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");
        user.setRoles(Collections.singleton(new Role(1L, "ADMIN")));

        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("jwtToken");
        when(jwtEncoder.encode(any(JwtEncoderParameters.class))).thenReturn(jwt);

        when(valueOperations.set(anyString(), any(), any(Duration.class))).thenReturn(Mono.just(true));

        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "password");

        Mono<ResponseEntity<ResponseTokenDTO>> response = authController.login(loginRequest);

        StepVerifier.create(response)
                .expectNextMatches(entity -> entity.getStatusCode().is2xxSuccessful() &&
                        entity.getBody() != null &&
                        "jwtToken".equals(entity.getBody().accessToken()))
                .verifyComplete();
    }

    @Test
    void loginUserNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Mono.empty());

        LoginRequestDTO loginRequest = new LoginRequestDTO("unknown@example.com", "password");

        Mono<ResponseEntity<ResponseTokenDTO>> response = authController.login(loginRequest);

        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof Exception &&
                        "Usuario nao encontrado".equals(throwable.getMessage()))
                .verify();
    }

    @Test
    void loginIncorrectPassword() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setPassword("encodedPassword");

        when(userRepository.findByEmail(anyString())).thenReturn(Mono.just(user));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        LoginRequestDTO loginRequest = new LoginRequestDTO("test@example.com", "wrongPassword");

        Mono<ResponseEntity<ResponseTokenDTO>> response = authController.login(loginRequest);

        StepVerifier.create(response)
                .expectErrorMatches(throwable -> throwable instanceof Exception &&
                        "Senha incorreta".equals(throwable.getMessage()))
                .verify();
    }
}
