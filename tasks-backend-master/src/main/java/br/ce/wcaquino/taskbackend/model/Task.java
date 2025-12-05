package br.ce.wcaquino.taskbackend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity(name = "tasks_table")
public class Task {

    public Task() {
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Nome da tarefa deve ser preenchido.")
    private String nome;

    @Column(nullable = false)
    @FutureOrPresent(message = "A data da tarefa deve ser no presente ou no futuro.")
    private LocalDateTime dataConclusao;

    @Column(nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(nullable = false)
    @NotBlank(message = "Descrição da tarefa deve ser preenchida.")
    private String descricao;

    @Column(nullable = false)
    @NotNull(message = "O status deve ser: PENDENTE, CONCLUIDA ou ATRASADA.")
    @Enumerated(EnumType.STRING)
    private Status status;
}
