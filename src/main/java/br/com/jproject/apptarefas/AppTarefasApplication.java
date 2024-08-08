package br.com.jproject.apptarefas;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;


@SpringBootApplication
@EnableMongoRepositories
public class AppTarefasApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppTarefasApplication.class, args);
    }

}
