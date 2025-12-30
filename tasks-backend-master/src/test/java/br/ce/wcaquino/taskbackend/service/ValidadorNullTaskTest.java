package br.ce.wcaquino.taskbackend.service;

import br.ce.wcaquino.taskbackend.dto.TaskCreateDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static br.ce.wcaquino.taskbackend.model.Status.ATRASADA;
import static br.ce.wcaquino.taskbackend.model.Status.CONCLUIDA;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ValidadorNullTaskTest {

    @InjectMocks
    private ValidadorNullTask validadorNullTask;

    @Test
    @DisplayName("DTO passou na validação.")
    void validarSuccess() {
        // ARRANGE
        TaskCreateDTO dto = new TaskCreateDTO("task", LocalDateTime.now().plusDays(2), "descricao", CONCLUIDA);

        // ACT + ASSERT
        Assertions.assertDoesNotThrow(() -> validadorNullTask.validarCampos(dto.nome(), dto.descricao(), dto.dataConclusao(), dto.status()));
    }

    @Test
    @DisplayName("Deve lançar erro quando DTO inválido")
    void validarError() {
        // ARRANGE
        TaskCreateDTO dto = new TaskCreateDTO("", LocalDateTime.now().minusDays(2), "", ATRASADA);

        // ACT + ASSERT
        assertThrows(IllegalArgumentException.class, () -> validadorNullTask.validarCampos(dto.nome(), dto.descricao(), dto.dataConclusao(), dto.status()));
    }
}