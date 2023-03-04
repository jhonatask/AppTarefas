package br.com.jproject.apptarefas.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Task {

    public static final String PENDENTE = "P";
    public static final String CONCLUIDA = "C";
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    @Column(nullable = false, length = 1000)
    private String description;
    @Column(nullable = false)
    private String priority;
    @Column(nullable = false)
    private String status;
}
