package br.ce.wcaquino.taskbackend.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDateTime;

public record TaskCreateDTO(
        @NotBlank(message = "nome da tarefa deve ser preenchido")
        String nome,

        @FutureOrPresent(message = "A data da tarefa deve ser no presente ou no futuro.")
        LocalDateTime dataConclusao,

        @NotBlank(message = "Descrição da tarefa deve ser preenchida.")
        String descricao,

        @NotNull(message = "O status deve ser: PENDENTE, CONCLUIDA ou ATRASADA.")
        br.ce.wcaquino.taskbackend. model.Status status
) { }