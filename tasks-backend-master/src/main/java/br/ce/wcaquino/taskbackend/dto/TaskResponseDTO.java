package br.ce.wcaquino.taskbackend.dto;

import br.ce.wcaquino.taskbackend.model.Status;
import br.ce.wcaquino.taskbackend.model.Task;

import java.time.LocalDateTime;

public record TaskResponseDTO(String nome, LocalDateTime dataConclusao, String descricao, Status status) {

    public TaskResponseDTO(Task task) {
        this(task.getNome(), task.getDataConclusao(), task.getDescricao(), task.getStatus());
    }


}
