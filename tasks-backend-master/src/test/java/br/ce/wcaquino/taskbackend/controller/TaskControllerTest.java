package br.ce.wcaquino.taskbackend.controller;

import br.ce.wcaquino.taskbackend.advice.exceptions.TaskNotFoundException;
import br.ce.wcaquino.taskbackend.dto.TaskCreateDTO;
import br.ce.wcaquino.taskbackend.dto.TaskResponseDTO;
import br.ce.wcaquino.taskbackend.dto.TaskUpdateDTO;
import br.ce.wcaquino.taskbackend.model.Task;
import br.ce.wcaquino.taskbackend.service.TaskService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static br.ce.wcaquino.taskbackend.model.Status.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//@SpringBootTest + @AutoConfigureMockMvc -> são usados geralmente para teste de integração,
// mas no nosso caso testaremos métodos do controller apenas

@WebMvcTest(TaskController.class)
@AutoConfigureJsonTesters
class TaskControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private TaskService taskService;

    @Autowired
    private JacksonTester<TaskCreateDTO> jacksonTester;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("SUCESSO 200 - retorna todas as tasks.")
    void getAllTasksSuccess() throws Exception {

        TaskCreateDTO dto = new TaskCreateDTO("Estudar Java",
                LocalDateTime.now().plusDays(2),
                "Testes Unitários e de Integração",
                PENDENTE);

        TaskCreateDTO dto2 = new TaskCreateDTO("Estudar C",
                LocalDateTime.now().plusDays(1),
                "rever ponteiros",
                PENDENTE);

        TaskResponseDTO taskResponseDTO1 = new TaskResponseDTO(new Task(dto));
        TaskResponseDTO taskResponseDTO2 = new TaskResponseDTO(new Task(dto2));
        List<TaskResponseDTO> taskResponseList = List.of(taskResponseDTO1, taskResponseDTO2);

        when(taskService.getAllTasks()).thenReturn(taskResponseList);

        // ACT + ASSERT
        mvc.perform(get("/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(taskResponseList.size()))
                .andExpect(jsonPath("$[0].nome").value(taskResponseDTO1.nome()))
                .andExpect(jsonPath("$[1].nome").value(taskResponseDTO2.nome()))
                .andReturn();
    }

    @Test
    @DisplayName("ERRO 400 - tentando trazer as tasks sem nenhuma task registrada.")
    void getAllTasksErrorThrowsTNFE() throws Exception {
        // ARRANGE
        when(taskService.getAllTasks()).thenThrow(TaskNotFoundException.class);

        // ASSERT + ACT
        mvc.perform(get("/tasks")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertInstanceOf(TaskNotFoundException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    @DisplayName("SUCESSO 200 - sucesso ao buscar task por ID")
    void getTaskByIdSuccess() throws Exception {
        // ARRANGE
        Task task = new Task(new TaskCreateDTO("tarefa",
                LocalDateTime.now().plusDays(1),
                "descricao",
                PENDENTE));

        task.setId(2L);
        TaskResponseDTO taskResponseDTO = new TaskResponseDTO(task);

        // ACT
        when(taskService.getTaskById(task.getId())).thenReturn(taskResponseDTO);

        // ASSERT
        mvc.perform(get("/tasks/{id}", task.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("ERRO 400 - erro ao buscar task inexistente, lança TNFE.")
    void getTaskByIdErrorThrowsTNFE() throws Exception {
        // ARRANGE
        when(taskService.getTaskById(2L)).thenThrow(TaskNotFoundException.class);

        // ASSERT + ACT
        mvc.perform(get("/tasks/{id}", 2L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertInstanceOf(TaskNotFoundException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    @DisplayName("SUCESSO 200 - retorna lista de tasks através do Status")
    void getTaskByStatusSuccess() throws Exception {
        // ARRANGE
        TaskCreateDTO dto = new TaskCreateDTO("Estudar Java",
                LocalDateTime.now().plusDays(2),
                "Testes Unitários e de Integração",
                PENDENTE);

        TaskCreateDTO dto2 = new TaskCreateDTO("Estudar C",
                LocalDateTime.now().plusDays(1),
                "rever ponteiros",
                PENDENTE);

        TaskResponseDTO taskResponseDTO1 = new TaskResponseDTO(new Task(dto));
        TaskResponseDTO taskResponseDTO2 = new TaskResponseDTO(new Task(dto2));
        List<TaskResponseDTO> taskResponseList = List.of(taskResponseDTO1, taskResponseDTO2);

        when(taskService.getTaskByStatus(dto.status())).thenReturn(taskResponseList);

        // ACT + ASSERT
        mvc.perform(
                        get("/tasks/status")
                                .param("status", "PENDENTE")  // .param() -> usado quando o metodo no controller usa @RequestParam
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(taskResponseList.size()))
                .andReturn();
    }

    @Test
    @DisplayName("Erro 400 - não localiza task pelo status e lança TNFE")
    void getTaskByStatusErrorTNFE() throws Exception {
        // ARRANGE
        when(taskService.getTaskByStatus(PENDENTE))
                .thenThrow(TaskNotFoundException.class);

        // ACT + ASSERT
        mvc.perform(
                        get("/tasks/status")
                                .param("status", "PENDENTE")  // .param() -> usado quando o metodo no controççer usa @RequestParam
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(status().is4xxClientError())
                .andExpect(result -> assertInstanceOf(TaskNotFoundException.class, result.getResolvedException()))
                .andReturn();
    }

    @Test
    @DisplayName("CRIADO/SUCESSO 201 - cria task corretamente")
    void createTaskSuccess() throws Exception {
        // ARRANGE
        /*
        String json = """
        {
          "nome": "Estudar Java",
          "dataConclusao": "2030-12-31T10:00:00",
          "descricao": "Estudar validação com Spring",
          "status": "PENDENTE"
        }
        """;

        cria JSON MANUAL */

        TaskCreateDTO dto = new TaskCreateDTO("Estudar Java",
                LocalDateTime.now().plusDays(2),
                "Testes Unitários e de Integração",
                PENDENTE);

        // ACT
        var response = mvc.perform(
                        post("/tasks")
                                .content(jacksonTester.write(dto).getJson()) // jacksonTester.write(dto).getJson() -> cria um JSON com os atributos de dto
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        assertEquals(201, response.getStatus());
    }

    @Test
    @DisplayName("ERRO 400 - dados inválidos para criação de task")
    void createTaskErrorInvalidJson() throws Exception {
        // ARRANGE
        String json = "{}";

        // ACT
        var response = mvc.perform(
                        post("/tasks")
                                .content(json)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        assertEquals(400, response.getStatus());
    }

    @Test
    @DisplayName("SUCESSO 200 - Criando lista de task com sucesso")
    void createTaskList() throws Exception {
        // ARRANGE
        TaskCreateDTO dto = new TaskCreateDTO("Estudar Java",
                LocalDateTime.now().plusDays(2),
                "Testes Unitários e de Integração",
                PENDENTE);

        TaskCreateDTO dto2 = new TaskCreateDTO("Estudar Java",
                LocalDateTime.now().plusDays(2),
                "Testes Unitários e de Integração",
                PENDENTE);

        TaskResponseDTO taskResponseDTO1 = new TaskResponseDTO(new Task(dto));
        TaskResponseDTO taskResponseDTO2 = new TaskResponseDTO(new Task(dto2));

        List<TaskResponseDTO> list = List.of(taskResponseDTO1, taskResponseDTO2);

        // ACT
        var response = mvc.perform(
                        post("/tasks/list")
                                .content(objectMapper.writeValueAsString(list))
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn()
                .getResponse();

        // ASSERT
        assertEquals(201, response.getStatus());
    }

    @Test
    @DisplayName("ERRO 400 - erro ao criar lista de task")
    void createTaskListError() throws Exception {
        // ARRANGE
        TaskCreateDTO json = new TaskCreateDTO(null, null, null, null);
        TaskCreateDTO json1 = new TaskCreateDTO(null, null, null, null);
        List<TaskCreateDTO> list = List.of(json, json1);

        // ACT
        var response = mvc.perform(
                        post("/tasks/list")
                                .content(objectMapper.writeValueAsString(list)) // jacksonTester.write(dto).getJson() -> cria um JSON com os atributos de dto
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        assertEquals(400, response.getStatus());
    }

    @Test
    @DisplayName("200 ATUALIZADO - atualizando task com sucesso.")
    void updateTaskByIdSuccess() throws Exception {
        // ARRANGE
        Task task = new Task(new TaskCreateDTO("t", LocalDateTime.now().plusDays(4), "d", PENDENTE));
        task.setId(1L);
        TaskUpdateDTO taskUpdateDTO = new TaskUpdateDTO("tUpdate", LocalDateTime.now().plusDays(2), "descUpdate", ATRASADA);

        TaskResponseDTO taskResponseDTO = new TaskResponseDTO(task);
        when(taskService.putTaskById(task.getId(), taskUpdateDTO)).thenReturn(taskResponseDTO);

        // ACT
        var response = mvc.perform(
                        put("/tasks/{id}", task.getId())
                                .content(objectMapper.writeValueAsString(taskUpdateDTO))
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        assertEquals(200, response.getStatus());
    }

    @Test
    @DisplayName("400 ERRO AO ATUALIZAR - nova task com atributos invalidos para atualização.")
    void updateTaskByIdErrorBodyInvalid() throws Exception {
        // ARRANGE
        Long id = (1L);
        TaskUpdateDTO taskUpdateDTO = new TaskUpdateDTO(null, null, null, null);

        // ACT
        var response = mvc.perform(
                        put("/tasks/{id}", id)
                                .content(objectMapper.writeValueAsString(taskUpdateDTO))
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        assertEquals(400, response.getStatus());
    }

    @Test
    @DisplayName("404 ERRO AO ATUALIZAR - não localizando task para atualizar.")
    void updateTaskByIdErrorThrowsTNFE() throws Exception {
        // ARRANGE
        TaskUpdateDTO taskUpdateDTO = new TaskUpdateDTO("tarefa",
                LocalDateTime.now().plusDays(4),
                "descricao",
                PENDENTE);

        when(taskService.putTaskById(eq(1L), any(TaskUpdateDTO.class)))
                .thenThrow(TaskNotFoundException.class);

        // ACT
        var response = mvc.perform(
                        put("/tasks/{id}", 1L)
                                .content(objectMapper.writeValueAsString(taskUpdateDTO))
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        assertEquals(404, response.getStatus());
    }

    @Test
    @DisplayName("204 DELETADO - deletando task com sucesso.")
    void deleteTaskByIdSuccess() throws Exception {
        // ARRANGE
        Long id = (1L);
        doNothing().when(taskService).deleteTaskById(id);

        // ACT
        var response = mvc.perform(
                        delete("/tasks/{id}", id)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        assertEquals(204, response.getStatus());
    }

    @Test
    @DisplayName("404 NAO DELETADO - task não localizada para DELETE.")
    void deleteTaskByIdErrorThrowsTNFE() throws Exception {
        // ARRANGE
        doThrow(TaskNotFoundException.class).when(taskService).deleteTaskById(1L);

        // ACT
        var response = mvc.perform(
                        delete("/tasks/{id}", 1L)
                                .contentType(MediaType.APPLICATION_JSON))
                .andReturn().getResponse();

        // ASSERT
        assertEquals(404, response.getStatus());
    }
}