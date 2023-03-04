package br.com.jproject.apptarefas.services;

import br.com.jproject.apptarefas.dto.TaskDTO;
import br.com.jproject.apptarefas.entity.Task;
import br.com.jproject.apptarefas.mapper.TaskMapperDTO;
import br.com.jproject.apptarefas.repository.TaskRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@Service
public class TaskService {


    private final TaskRepository taskRepository;

    private final TaskMapperDTO taskMapperDTO;

    public TaskService(TaskRepository taskRepository, TaskMapperDTO taskMapperDTO) {
        this.taskRepository = taskRepository;
        this.taskMapperDTO = taskMapperDTO;
    }

    public Page<TaskDTO> findTaskAllOrFilter(String status, Pageable pageable){
        Page<Task> result;
        result = (status == null ? taskRepository.findAll(pageable) : taskRepository.findByStatusContaining(status, pageable));
        return result.map(taskMapperDTO::taskTotaskDTO);
    }

    @Transactional
    public TaskDTO createTask(TaskDTO taskDTO) {
        Task task = taskMapperDTO.taskDtoToTask(taskDTO);
        task.setName(taskDTO.name);
        task.setDescription(taskDTO.description);
        task.setPriority(taskDTO.priority);
        task.setStatus(Task.PENDENTE);
        taskRepository.save(task);
        return taskMapperDTO.taskTotaskDTO(task);
    }

    @Transactional
    public void deleteTask(Integer id) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()) taskRepository.delete(task.get());
    }

    @Transactional
    public TaskDTO updateTask(Integer id, TaskDTO taskDTO) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()){
            Task replaceTask = task.get();
            replaceTask.setPriority(taskDTO.priority);
            replaceTask.setDescription(taskDTO.description);
            taskRepository.save(replaceTask);
            return taskMapperDTO.taskTotaskDTO(replaceTask);
        }
        return null;
    }

    @Transactional
    public TaskDTO makeTaskConcluded(Integer id) {
        Optional<Task> task = taskRepository.findById(id);
        if (task.isPresent()){
            Task replaceTask = task.get();
            replaceTask.setStatus(Task.CONCLUIDA);
            taskRepository.save(replaceTask);
            return taskMapperDTO.taskTotaskDTO(replaceTask);
        }
        return null;
    }
}
