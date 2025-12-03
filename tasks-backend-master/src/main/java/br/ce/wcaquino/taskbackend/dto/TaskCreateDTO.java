package br.ce.wcaquino.taskbackend.dto;

import java.time.LocalDateTime;

public record TaskCreateDTO(String nome, LocalDateTime dataConclusao, String descricao,
                            br.ce.wcaquino.taskbackend. model.Status status) {
}