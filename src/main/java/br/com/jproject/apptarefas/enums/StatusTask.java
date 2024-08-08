package br.com.jproject.apptarefas.enums;

import lombok.Getter;

@Getter
public enum StatusTask {
    OPEN("Open"),
    IN_PROGRESS("In Progress"),
    DONE("Done");

    private final String status;

    StatusTask(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
