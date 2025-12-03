package br.ce.wcaquino.taskbackend.dto;

import br.ce.wcaquino.taskbackend.model.Task;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.BeanUtils;

import java.time.LocalDate;

@Getter
@Setter
public record TaskResponseDTO(Long id, String nome, LocalDate data, String descricao,
                      br.ce.wcaquino.taskbackend.model.Task.Status status) {

    public TaskResponseDTO(Task task) {
        this(task.getId(), task.getNome(), task.getData(), task.getDescricao(), task.getStatus());
    }
}
