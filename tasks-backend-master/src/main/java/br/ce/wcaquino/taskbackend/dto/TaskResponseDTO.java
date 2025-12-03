package br.ce.wcaquino.taskbackend.dto;

import br.ce.wcaquino.taskbackend.model.Task;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public record TaskResponseDTO(Long id, String nome, LocalDateTime dataCriacao, LocalDateTime dataConclusao, String descricao,
                      br.ce.wcaquino.taskbackend.model.Task.Status status) {

    public TaskResponseDTO(Task task) {
        this(task.getId(), task.getNome(), task.getDataCriacao(), task.getDataConclusao(), task.getDescricao(), task.getStatus());
    }

}
