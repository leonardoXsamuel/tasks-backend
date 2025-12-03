package br.ce.wcaquino.taskbackend.dto;

import java.time.LocalDateTime;

public record TaskCreateDTO(Long id, String nome, LocalDateTime dataConclusao, String descricao,
                            br.ce.wcaquino.taskbackend.model.Task.Status status) {
}