package br.com.jproject.apptarefas.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    public UUID id;
    public String name;
    public String description;
    public String priority;
    public String status;
    public String fileUrl;
}
