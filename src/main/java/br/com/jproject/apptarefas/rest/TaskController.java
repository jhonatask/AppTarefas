package br.com.jproject.apptarefas.rest;


import br.com.jproject.apptarefas.dto.request.TaskRequestDTO;
import br.com.jproject.apptarefas.dto.response.TaskDTO;
import br.com.jproject.apptarefas.services.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping(value = "/task")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @Operation(summary = "Retorna um lista contendo todas as tarefas ou filtrando pelo status da Open ou Done")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Listando tarefas com sucesso",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Error",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Error",
                    content = @Content) })
    public Flux<ResponseEntity<TaskDTO>> findTask(@RequestParam(required = false) String status) {
        return taskService.findTaskAllOrFilter(status)
                .map(tasks -> ResponseEntity.status(HttpStatus.OK).body(tasks));
    }


    @Operation(summary = "Cadastra um nova tarefa com status open")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso ao cadastrar um tarefa",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Error",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Error",
                    content = @Content) })
    @PostMapping(consumes = { "multipart/form-data" })
    @PreAuthorize("hasAuthority('scope_BASIC')")
    public Mono<ResponseEntity<TaskDTO>> createTask(@RequestPart("task") TaskRequestDTO taskDTO,
                                                    @RequestPart("file") MultipartFile file) {
        return taskService.createTask(taskDTO, file)
                .map(task -> ResponseEntity.status(HttpStatus.CREATED).body(task));
    }

    @PutMapping(path = "/{id}")
    @Operation(summary = "Atualiza a descrição da tarefa e (ou) sua prioridade caso ela exista")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso ao atualizar uma tarefa",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Error",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Error",
                    content = @Content) })
    public Mono<ResponseEntity<TaskDTO>> updateTask(@PathVariable UUID id, @RequestBody TaskRequestDTO taskDTO) {
        return taskService.updateTask(id, taskDTO)
                .map(task -> ResponseEntity.status(HttpStatus.OK).body(task))
                .switchIfEmpty(Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).build()));
    }


    @Operation(summary = "Atualiza a tarefa para status concluida caso ela exista")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Sucesso ao atualizar uma tarefa",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Error",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Error",
                    content = @Content) })
    @PatchMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('scope_BASIC')")
    public Mono<ResponseEntity<TaskDTO>> makeTaskConcluded(@PathVariable UUID id) {
        return taskService.makeTaskConcluded(id)
                .map(taskDTO -> ResponseEntity.status(HttpStatus.OK).body(taskDTO))
                .switchIfEmpty(Mono.error(new RuntimeException("Não foi encontrado tarefas para esta id:" + id)));
    }


    @Operation(summary = "Deleta uma tarefa informada caso ela exista")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Sucesso ao deletar a tarefa",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskDTO.class)) }),
            @ApiResponse(responseCode = "400", description = "Error",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Error",
                    content = @Content) })
    @DeleteMapping(path = "/{id}")
    @PreAuthorize("hasAuthority('scope_ADMIN')")
    public Mono<ResponseEntity<Void>> deleteTask(@PathVariable UUID id) {
        return taskService.deleteTask(id)
                .then(Mono.just(new ResponseEntity<>(HttpStatus.NO_CONTENT)));
    }

}
