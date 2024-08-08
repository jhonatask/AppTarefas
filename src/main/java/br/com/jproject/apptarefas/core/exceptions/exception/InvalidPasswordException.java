package br.com.jproject.apptarefas.core.exceptions.exception;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(final String message) {
        super(message);
    }

    public InvalidPasswordException(String message, Throwable cause){
        super(message, cause);
    }

    public InvalidPasswordException() {

    }
}
