package br.ce.wcaquino.taskbackend.service;

import br.ce.wcaquino.taskbackend.advice.exceptions.TaskNotFoundException;
import br.ce.wcaquino.taskbackend.dto.TaskCreateDTO;
import br.ce.wcaquino.taskbackend.dto.TaskResponseDTO;
import br.ce.wcaquino.taskbackend.dto.TaskUpdateDTO;
import br.ce.wcaquino.taskbackend.model.Task;
import br.ce.wcaquino.taskbackend.repo.TaskRepo;
import org.h2.command.dml.MergeUsing;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static br.ce.wcaquino.taskbackend.model.Status.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @InjectMocks
    private TaskService taskService;

    @Mock
    private TaskRepo taskRepo;

    @Mock
    private ValidadorNullTask validadorNullTask;

    @Test
    @DisplayName("sucesso ao criar task")
    void createTaskSuccess() {
        // ARRANGE
        TaskCreateDTO dto = new TaskCreateDTO("task", LocalDateTime.now().plusDays(1), "desc", ATRASADA);

        Task task = new Task(dto);

        when(taskRepo.save(any(Task.class))).thenReturn(task);

        // ACT
        TaskResponseDTO taskResponseDTO = taskService.createTask(dto);

        // ASSERT
        Assertions.assertEquals(taskResponseDTO.nome(), dto.nome());
        Assertions.assertEquals(taskResponseDTO.descricao(), dto.descricao());
        Assertions.assertEquals(taskResponseDTO.status(), dto.status());
        verify(taskRepo).save(any(Task.class));
        verify(validadorNullTask).validarCampos(dto.nome(), dto.descricao(), dto.dataConclusao(), dto.status());
    }

    @Test
    @DisplayName("erro ao criar task - atributos nulos")
    void createTaskErrorNullable() {
        TaskCreateDTO dto = new TaskCreateDTO(null, LocalDateTime.now().plusDays(1), "desc", ATRASADA);

        doThrow(new IllegalArgumentException()).when(validadorNullTask).validarCampos(dto.nome(), dto.descricao(), dto.dataConclusao(), dto.status());

        Assertions.assertThrows(IllegalArgumentException.class, () -> taskService.createTask(dto));
        verify(taskRepo, never()).save(any());
    }

    @Test
    @DisplayName("sucesso ao criar lista de tasks")
    void createTaskListSuccess() {

        // ARRANGE (pré teste. onde organizamos tudo e criamos cenários com métodos STUBBING)
        TaskCreateDTO dto1 = new TaskCreateDTO("tarefa1", LocalDateTime.now().plusDays(1), "desc1", ATRASADA);
        TaskCreateDTO dto2 = new TaskCreateDTO("tarefa2", LocalDateTime.now().plusDays(3), "desc2", CONCLUIDA);
        List<TaskCreateDTO> dtoList = List.of(dto1, dto2);

        Task t1 = new Task(dto1);
        Task t2 = new Task(dto2);
        List<Task> taskList = List.of(t1, t2);

        when(taskRepo.saveAll(any(List.class))).thenReturn(taskList);

        // ACT (testa o método do service)
        List<TaskResponseDTO> taskResponseDTO = taskService.createTaskList(dtoList);

        // ASSERT (assertivas e verificações)
        Assertions.assertEquals(taskResponseDTO.size(), taskList.size());

        for (int i = 0; i < dtoList.size(); i++) {
            Assertions.assertEquals(dtoList.get(i).nome(), taskResponseDTO.get(i).nome());
            Assertions.assertEquals(dtoList.get(i).status(), taskResponseDTO.get(i).status());
            Assertions.assertEquals(dtoList.get(i).descricao(), taskResponseDTO.get(i).descricao());
            Assertions.assertEquals(dtoList.get(i).dataConclusao(), taskResponseDTO.get(i).dataConclusao());
        }

        verify(taskRepo).saveAll(any(List.class));

        for (TaskCreateDTO dto : dtoList) {
            verify(validadorNullTask).validarCampos(dto.nome(), dto.descricao(), dto.dataConclusao(), dto.status());
        }
    }

    @Test
    @DisplayName("erro ao criar lista de tasks")
    void createTaskListError() {

        // ARRANGE (pré teste. onde organizamos tudo e criamos cenários com métodos STUBBING)
        TaskCreateDTO dto1 = new TaskCreateDTO(null, LocalDateTime.now().plusDays(1), "desc1", ATRASADA);
        TaskCreateDTO dto2 = new TaskCreateDTO("tarefa2", LocalDateTime.now().plusDays(3), null, CONCLUIDA);

        List<TaskCreateDTO> dtoList = List.of(dto1, dto2);

        doThrow(new IllegalArgumentException())
                .when(validadorNullTask)
                .validarCampos(any(), any(), any(), any());

        Assertions.assertThrows(IllegalArgumentException.class, () -> taskService.createTaskList(dtoList));
        verify(taskRepo, never()).save(any());
    }

    @Test
    @DisplayName("sucesso ao retornar task por ID")
    void getTaskByIdSuccess() {
        // ARRANGE
        TaskCreateDTO dto = new TaskCreateDTO("tarefa", LocalDateTime.now().plusDays(3), "desc", CONCLUIDA);

        Task task = new Task(dto);
        task.setId(1L);

        when(taskRepo.findById(task.getId())).thenReturn(Optional.of(task));

        // ASSERT + ACT
        Assertions.assertDoesNotThrow(() -> taskService.getTaskById(task.getId()));
        Assertions.assertEquals(dto.nome(), task.getNome());
        Assertions.assertEquals(dto.descricao(), task.getDescricao());
        Assertions.assertEquals(dto.status(), task.getStatus());
        Assertions.assertEquals(dto.dataConclusao(), task.getDataConclusao());

        verify(taskRepo).findById(task.getId());
    }

    @Test
    @DisplayName("erro ao retornar task por ID - lança TNFE")
    void getTaskByIdThrowsTNFE() {

        // ASSERT + ACT
        Assertions.assertThrows(TaskNotFoundException.class, () -> taskService.getTaskById(30L));
        verify(taskRepo).findById(30L);
    }

    @Test
    @DisplayName("sucesso ao retornar task por STATUS")
    void getTaskByStatusSucess() {

        // ARRANGE
        TaskCreateDTO dto = new TaskCreateDTO("tarefa1", LocalDateTime.now().plusDays(3), "desc", CONCLUIDA);
        TaskCreateDTO dto2 = new TaskCreateDTO("tarefa2", LocalDateTime.now().plusDays(3), "desc2", CONCLUIDA);
        TaskCreateDTO dto3 = new TaskCreateDTO("tarefa3", LocalDateTime.now().plusDays(7), "desc3", CONCLUIDA);

        Task task = new Task(dto);
        Task task2 = new Task(dto2);
        Task task3 = new Task(dto3);
        task.setId(1L);
        task2.setId(2L);
        task3.setId(3L);

        List<Task> taskList = List.of(task, task2, task3);

        when(taskRepo.findAll()).thenReturn(List.of(task, task2, task3));

        List<TaskResponseDTO> taskResponseDTOS = taskService.getTaskByStatus(CONCLUIDA);

        // ASSERT + ACT
        for (int i = 0; i < taskResponseDTOS.size(); i++) {
            Assertions.assertEquals(taskResponseDTOS.get(i).nome(), taskList.get(i).getNome());
            Assertions.assertEquals(taskResponseDTOS.get(i).dataConclusao(), taskList.get(i).getDataConclusao());
            Assertions.assertEquals(taskResponseDTOS.get(i).descricao(), taskList.get(i).getDescricao());
            Assertions.assertEquals(taskResponseDTOS.get(i).status(), taskList.get(i).getStatus());
        }

        verify(taskRepo).findAll();
    }

    @Test
    @DisplayName("erro ao retornar task por STATUS - lança TNFE")
    void getTaskByStatusThrowsTNFE() {
        when(taskRepo.findAll()).thenReturn(List.of());

        Assertions.assertThrows(TaskNotFoundException.class, () -> taskService.getTaskByStatus(ATRASADA));

        verify(taskRepo).findAll();
    }

    @Test
    @DisplayName("Retornando todas as TASKS")
    void getAllTasksSucess() {
        // ARRANGE
        TaskCreateDTO dto = new TaskCreateDTO("t1", LocalDateTime.now().plusDays(1), "d1", CONCLUIDA);
        TaskCreateDTO dto2 = new TaskCreateDTO("t2", LocalDateTime.now().plusDays(2), "d3", CONCLUIDA);
        TaskCreateDTO dto3 = new TaskCreateDTO("t3", LocalDateTime.now().plusDays(3), "d2", CONCLUIDA);

        Task task1 = new Task(dto);
        Task task2 = new Task(dto2);
        Task task3 = new Task(dto3);
        List<Task> taskList = List.of(task1, task2, task3);

        when(taskRepo.findAll()).thenReturn(taskList);

        // ACT
        taskService.getAllTasks();

        // ASSERT
        verify(taskRepo).findAll();
    }

    @Test
    @DisplayName("erro ao retornar TASKS - lança TNFE")
    void getAllTasksThrowsTNFE() {
        // ARRANGE
        when(taskRepo.findAll()).thenReturn(List.of());

        // ACT + ASSERT
        Assertions.assertThrows(TaskNotFoundException.class, () -> taskService.getAllTasks());
        verify(taskRepo).findAll();
    }

    @Test
    @DisplayName("atualiza task por ID com sucesso.")
    void putTaskByIdSuccess() {
        // ARRANGE
        TaskCreateDTO dtoAntigo = new TaskCreateDTO("tarefa1", LocalDateTime.now().plusDays(3), "desc", CONCLUIDA);

        Task task = new Task(dtoAntigo);
        task.setId(2L);

        TaskUpdateDTO dtoAtualizado = new TaskUpdateDTO("Estudar AWS",
                LocalDateTime.now().plusDays(7),
                "EC2 e RDS",
                CONCLUIDA);

        when(taskRepo.findById(task.getId())).thenReturn(Optional.of(task));
        when(taskRepo.save(any(Task.class))).thenReturn(task);

        taskService.putTaskById(task.getId(), dtoAtualizado);

        Assertions.assertEquals(dtoAtualizado.nome(), task.getNome());
        Assertions.assertEquals(dtoAtualizado.descricao(), task.getDescricao());
        Assertions.assertEquals(dtoAtualizado.status(), task.getStatus());
        Assertions.assertEquals(dtoAtualizado.descricao(), task.getDescricao());

        verify(taskRepo).findById(task.getId());
        verify(taskRepo).save(task);
    }

    @Test
    @DisplayName("erro ao atualizar task por ID - lança TNFE")
    void putTaskByIdThrowsTNFE() {
        // ARRANGE
        TaskUpdateDTO dtoAtualizado = new TaskUpdateDTO("Estudar AWS",
                LocalDateTime.now().plusDays(7),
                "EC2 e RDS",
                CONCLUIDA);

        when(taskRepo.findById(2L)).thenReturn(Optional.empty());

        // ACT + ASSERT
        Assertions.assertThrows(TaskNotFoundException.class, () -> taskService.putTaskById(2L, dtoAtualizado));
        verify(taskRepo).findById(2L);
    }

    @Test
    @DisplayName("erro ao atualizar task por ID - quando atributos da nova task são nulos")
    void putTaskByIdErrorNullable() {
        // ARRANGE
        TaskCreateDTO dtoAntigo = new TaskCreateDTO("tarefa1", LocalDateTime.now().plusDays(3), "desc", CONCLUIDA);

        Task task = new Task(dtoAntigo);
        task.setId(2L);

        TaskUpdateDTO dtoAtualizado = new TaskUpdateDTO(null,
                LocalDateTime.now().plusDays(7),
                null,
                CONCLUIDA);

        when(taskRepo.findById(task.getId())).thenReturn(Optional.of(task));

        doThrow(new IllegalArgumentException())
                .when(validadorNullTask)
                .validarCampos(any(), any(), any(), any());

        // ACT + ASSERT
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> taskService.putTaskById(task.getId(), dtoAtualizado));

        verify(taskRepo).findById(task.getId());
        verify(taskRepo, never()).save(any(Task.class));
    }

    @Test
    @DisplayName("deletando task com sucesso.")
    void deleteTaskByIdSuccess() {
        // ARRANGE
        TaskCreateDTO dto = new TaskCreateDTO("tarefa",
                LocalDateTime.now().plusDays(7),
                "descricao",
                PENDENTE);

        Task task = new Task(dto);
        task.setId(2L);

        when(taskRepo.existsById(task.getId())).thenReturn(true);
        doNothing().when(taskRepo).deleteById(task.getId());

        // ACT
        taskService.deleteTaskById(task.getId());

        // ASSERT
        verify(taskRepo).existsById(task.getId());
        verify(taskRepo).deleteById(task.getId());
    }

    @Test
    @DisplayName("erro ao deletar task por id - lança TNFE")
    void deleteTaskByIdErrorThrowsTNFE() {
        // ARRANGE
        when(taskRepo.existsById(1L)).thenReturn(false);

        // ACT + ASSERT
        Assertions.assertThrows(TaskNotFoundException.class, () -> taskService.deleteTaskById(1L));
        verify(taskRepo).existsById(1L);
        verify(taskRepo, never()).deleteById(1L);
    }
}
