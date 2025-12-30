package br.ce.wcaquino.taskbackend.service;

import br.ce.wcaquino.taskbackend.dto.TaskCreateDTO;
import br.ce.wcaquino.taskbackend.model.Status;

import java.time.LocalDateTime;

public class ValidadorNullTask {

    public void validarCampos(String nome, String descricao, LocalDateTime dataConclusao, Status status) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException("Descrição é obrigatória");
        }
        if (dataConclusao == null || dataConclusao.isBefore(LocalDateTime.now().minusMinutes(2))) {
            throw new IllegalArgumentException("Data inválida");
        }
        if (status == null) {
            throw new IllegalArgumentException("Status inválido");
        }

    }
}