package br.ce.wcaquino.taskbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Task {

    public Task(String nome, LocalDate data, String descricao) {
        this.nome = nome;
        this.data = data;
        this.descricao = descricao;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Nome da tarefa deve ser preenchido.")
    private String nome;

    @Column(nullable = false)
    @FutureOrPresent(message = "A data da tarefa deve ser no presente ou no futuro.")
    private LocalDate data;

    @Column(nullable = false)
    @NotBlank(message = "Descrição da tarefa deve ser preenchida.")
    private String descricao;

    public enum Status {
        PENDENTE, CONCLUIDA, ATRASADA;
    }
}
