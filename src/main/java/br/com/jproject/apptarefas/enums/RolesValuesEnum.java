package br.com.jproject.apptarefas.enums;

import lombok.Getter;

@Getter
public enum RolesValuesEnum {
    ADMIN(1L),
    BASIC(2L);

    long id;

    RolesValuesEnum(long id) {
        this.id = id;
    }
}
