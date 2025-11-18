package br.ce.wcaquino.taskbackend.advice.exceptions;

public class TaskNotFoundException extends RuntimeException{

    public TaskNotFoundException() {
        super("a Task solicitada não foi localizada.");
    }

    public TaskNotFoundException(String message) {
        super(message);
    }
}
