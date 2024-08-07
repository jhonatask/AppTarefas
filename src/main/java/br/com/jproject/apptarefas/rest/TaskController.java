package br.com.jproject.apptarefas.rest;


import br.com.jproject.apptarefas.dto.TaskDTO;
import br.com.jproject.apptarefas.services.TaskService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@RequestMapping(value = "/task")
@Api(value = "Api Gerenciamento de Tarefas")
@CrossOrigin(origins = "*")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    @ApiOperation(value = "Retorna um lista contendo todas as tarefas ou filtrando pelo status da tarefa Ex: status=P ")
    public Mono<ResponseEntity<Page<TaskDTO>>> findTask(@RequestParam(required = false) String status, Pageable pageable) {
        return taskService.findTaskAllOrFilter(status, pageable)
                .map(tasks -> ResponseEntity.status(HttpStatus.OK).body(tasks));
    }

    @PostMapping
    @ApiOperation(value = "Cadastra um nova tarefa com status pedente")
    public Mono<ResponseEntity<TaskDTO>> createTask(@RequestBody TaskDTO taskDTO) {
        return taskService.createTask(taskDTO)
                .map(task -> ResponseEntity.status(HttpStatus.CREATED).body(task));
    }

    @PutMapping(path = "/{id}")
    @ApiOperation(value = "Atualiza a descrição da tarefa e (ou) sua prioridade caso ela exista")
    public Mono<ResponseEntity<TaskDTO>> updateTask(@PathVariable UUID id, @RequestBody TaskDTO taskDTO) {
        return taskService.updateTask(id, taskDTO)
                .map(task -> ResponseEntity.status(HttpStatus.OK).body(task))
                .switchIfEmpty(Mono.error(new RuntimeException("Não foi encontrado tarefas para esta id:" + id)));
    }

    @PatchMapping(path = "/{id}")
    @ApiOperation(value = "Atualiza a tarefa para status concluida caso ela exista")
    public Mono<ResponseEntity<TaskDTO>> makeTaskConcluded(@PathVariable UUID id) {
        return taskService.makeTaskConcluded(id)
                .map(taskDTO -> ResponseEntity.status(HttpStatus.OK).body(taskDTO))
                .switchIfEmpty(Mono.error(new RuntimeException("Não foi encontrado tarefas para esta id:" + id)));
    }

    @DeleteMapping(path = "/{id}")
    @ApiOperation(value = "Deleta uma tarefa informada caso ela exista")
    public Mono<ResponseEntity<Void>> deleteTask(@PathVariable UUID id) {
        return taskService.deleteTask(id)
                .then(Mono.just(new ResponseEntity<>(HttpStatus.NO_CONTENT)));
    }

}
