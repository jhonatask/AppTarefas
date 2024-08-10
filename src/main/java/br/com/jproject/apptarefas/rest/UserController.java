package br.com.jproject.apptarefas.rest;

import br.com.jproject.apptarefas.dto.request.UserRequestDTO;
import br.com.jproject.apptarefas.dto.response.UserResponseDTO;
import br.com.jproject.apptarefas.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Cadastra um novo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso ao cadastrar um usuario",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Error",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Error",
                    content = @Content) })
    @PostMapping
    public Mono<ResponseEntity<UserResponseDTO>> createUser(@Valid @RequestBody UserRequestDTO user) throws Exception {
        return userService.createUser(user)
                .map(newUser -> ResponseEntity.status(HttpStatus.CREATED).body((UserResponseDTO) newUser));
    }

    @Operation(summary = "Lista de usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Sucesso ao listar usuarios",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Error",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Error",
                    content = @Content) })
    @GetMapping
    @PreAuthorize("hasAuthority('scope_ADMIN')")
    public ResponseEntity<Flux<UserResponseDTO>> getAllUsers() {
        Flux<UserResponseDTO> users = userService.getAllUsers();
        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @Operation(summary = "Buscar um usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso ao buscar um usuario",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Error",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Error",
                    content = @Content) })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<UserResponseDTO>> getUserById(@PathVariable UUID id) {
        return userService.getUserById(id)
                .map(user -> ResponseEntity.status(HttpStatus.OK).body(user))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Atualizar um novo usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso ao atualizar um usuario",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Error",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Error",
                    content = @Content) })
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('scope_ADMIN')")
    public Mono<ResponseEntity<UserResponseDTO>> updateUser(@PathVariable UUID id, @Valid @RequestBody UserRequestDTO userDetails) {
        return userService.updateUser(id, userDetails)
                .map(user -> ResponseEntity.status(HttpStatus.OK).body(user))
                .defaultIfEmpty(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @Operation(summary = "Deletar um usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sucesso ao deletar um usuario",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = UserResponseDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Error",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Error",
                    content = @Content) })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('scope_ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}