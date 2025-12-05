package br.ce.wcaquino.taskbackend.dto;

import br.ce.wcaquino.taskbackend.model.Status;

import java.time.LocalDateTime;

public record TaskUpdateDTO(String nome, LocalDateTime dataConclusao, String descricao, Status status) {

}
