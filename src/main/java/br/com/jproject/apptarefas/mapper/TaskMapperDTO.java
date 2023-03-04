package br.com.jproject.apptarefas.mapper;

import br.com.jproject.apptarefas.dto.TaskDTO;
import br.com.jproject.apptarefas.entity.Task;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface TaskMapperDTO {

    TaskDTO taskTotaskDTO(Task entity);
    Task taskDtoToTask(TaskDTO entity);
}
