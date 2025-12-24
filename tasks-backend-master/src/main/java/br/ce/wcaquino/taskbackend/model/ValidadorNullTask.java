package br.ce.wcaquino.taskbackend.model;

import br.ce.wcaquino.taskbackend.dto.TaskCreateDTO;

import java.time.LocalDateTime;

public class ValidadorNullTask {

    public void validar(TaskCreateDTO dto) {

        if (dto.nome() == null || dto.nome().isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (dto.descricao() == null || dto.descricao().isBlank()) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
        if (dto.dataConclusao() == null || dto.dataConclusao().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Data inválida");
        }
        if (dto.status() == null) {
            throw new IllegalArgumentException("Status inválido");
        }

    }
}