package br.com.jproject.apptarefas.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Getter
@AllArgsConstructor
@Document(collection = "roles")
public class Role {

    @Id
    private Long id;

    private String name;

}
