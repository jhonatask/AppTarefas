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

@RestController
@RequestMapping(value = "/task")
@Api(value = "Api Gerenciamento de Tarefas")
@CrossOrigin(origins = "*")
public class TaskController {

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    private final TaskService taskService;
    @GetMapping
    @ApiOperation(value = "Retorna um lista contendo todas as tarefas ou filtrando pelo status da tarefa Ex: status=P ")
    public ResponseEntity<Page<TaskDTO>> findTask(@RequestParam(required = false) String status , Pageable pageable){
        Page<TaskDTO> tasks = taskService.findTaskAllOrFilter(status,pageable);
        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }
    @PostMapping
    @ApiOperation(value = "Cadastra um nova tarefa com status pedente")
    public ResponseEntity<TaskDTO> createTask(@RequestBody TaskDTO taskDTO){
        TaskDTO task = taskService.createTask(taskDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping(path = "/{id}")
    @ApiOperation(value = "Atualiza a descrição da tarefa e (ou) sua prioridade caso ela exista")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Integer id, @RequestBody TaskDTO taskDTO){
        TaskDTO task = taskService.updateTask(id, taskDTO);
        if (task == null){
           throw new RuntimeException("Não foi encontrado tarefas para esta id:" + id);
        }
        return ResponseEntity.status(HttpStatus.OK).body(task);
    }
    @PatchMapping(path = "/{id}")
    @ApiOperation(value = "Atualiza a tarefa para status concluida caso ela exista")
    public ResponseEntity<TaskDTO> makeTaskConcluded(@PathVariable Integer id){
        TaskDTO taskDTO = taskService.makeTaskConcluded(id);
        if (taskDTO == null){
            throw new RuntimeException("Não foi encontrado tarefas para esta id:" + id);
        }
        return ResponseEntity.status(HttpStatus.OK).body(taskDTO);
    }
    @DeleteMapping(path = "/{id}")
    @ApiOperation(value = "Deleta uma tarefa informada caso ela exista")
    public ResponseEntity<Void> deleteTask(@PathVariable Integer id){
        taskService.deleteTask(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
