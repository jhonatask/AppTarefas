package br.com.jproject.apptarefas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO {
    public Integer id;
    public String name;
    public String description;
    public String priority;
    public String status;
}
