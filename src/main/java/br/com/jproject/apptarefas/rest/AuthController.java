package br.com.jproject.apptarefas.rest;

import br.com.jproject.apptarefas.dto.request.LoginRequestDTO;
import br.com.jproject.apptarefas.dto.response.ResponseTokenDTO;
import br.com.jproject.apptarefas.entities.Role;
import br.com.jproject.apptarefas.repository.UserRepository;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.core.ReactiveValueOperations;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final ReactiveRedisTemplate<String, Object> redisTemplate;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtEncoder jwtEncoder, ReactiveRedisTemplate<String, Object> redisTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.redisTemplate = redisTemplate;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<ResponseTokenDTO>> login(@RequestBody LoginRequestDTO body) {
        return userRepository.findByEmail(body.email())
                .switchIfEmpty(Mono.error(new Exception("Usuario nao encontrado")))
                .flatMap(user -> {
                    if (!passwordEncoder.matches(body.password(), user.getPassword())) {
                        return Mono.error(new Exception("Senha incorreta"));
                    }
                    var now = Instant.now();
                    var expiresIn = 300L;
                    var scopes = user.getRoles()
                            .stream()
                            .map(Role::getName)
                            .collect(Collectors.joining(" "));
                    var claims = JwtClaimsSet.builder()
                            .issuer("apptasks")
                            .subject(user.getId().toString())
                            .issuedAt(now)
                            .expiresAt(now.plusSeconds(expiresIn))
                            .claim("scope", scopes)
                            .build();

                    var jwtValue = jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
                    ReactiveValueOperations<String, Object> ops = redisTemplate.opsForValue();
                    return ops.set(jwtValue, user, Duration.ofSeconds(expiresIn))
                            .thenReturn(ResponseEntity.ok(new ResponseTokenDTO(jwtValue, expiresIn)));
                });
    }

}