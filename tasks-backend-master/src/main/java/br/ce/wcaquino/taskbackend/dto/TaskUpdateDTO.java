package br.ce.wcaquino.taskbackend.dto;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record TaskUpdateDTO(
        @NotBlank(message = "nome da tarefa deve ser preenchido")
        String nome,

        @FutureOrPresent(message = "A data da tarefa deve ser no presente ou no futuro.")
        LocalDateTime dataConclusao,

        @NotBlank(message = "Descrição da tarefa deve ser preenchida.")
        String descricao,

        @NotNull(message = "O status deve ser: PENDENTE, CONCLUIDA ou ATRASADA.")
        br.ce.wcaquino.taskbackend. model.Status status) {

}
