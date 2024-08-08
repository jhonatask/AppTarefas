package br.com.jproject.apptarefas.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "tasks")
public class Task {


    @Id
    private UUID id;
    private String name;
    private String description;
    private String priority;
    private String status;
}
