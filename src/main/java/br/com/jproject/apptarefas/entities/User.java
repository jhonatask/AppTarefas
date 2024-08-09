package br.com.jproject.apptarefas.entities;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.Date;
import java.util.Set;
import java.util.UUID;

@Data
@Document(collection = "users")
public class User {

    @Id
    private UUID id;

    @NotBlank(message = "O nome não pode estar em branco")
    @NotNull(message = "O nome não pode ser null")
    private String name;

    @Email(message = "O email deve ser válido")
    @NotBlank(message = "O email não pode estar em branco")
    private String email;

    @Field("password")
    private String password;

    @CreatedDate
    @Field("datacadastro")
    private Date datacadastro;

    @LastModifiedDate
    @Field("dataalteracao")
    private Date dataalteracao;

    private Set<Role> roles;

    public User() {
        this.id = UUID.randomUUID();
    }

}
