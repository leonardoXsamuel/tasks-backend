package br.ce.wcaquino.taskbackend.service;

import br.ce.wcaquino.taskbackend.advice.exceptions.TaskNotFoundException;
import br.ce.wcaquino.taskbackend.dto.TaskCreateDTO;
import br.ce.wcaquino.taskbackend.dto.TaskResponseDTO;
import br.ce.wcaquino.taskbackend.dto.TaskUpdateDTO;
import br.ce.wcaquino.taskbackend.model.Status;
import br.ce.wcaquino.taskbackend.model.Task;
import br.ce.wcaquino.taskbackend.repo.TaskRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class TaskService {

    private final TaskRepo taskRepo;

    public TaskService(TaskRepo taskRepo) {
        this.taskRepo = taskRepo;
    }

    public TaskResponseDTO createTask(TaskCreateDTO dto) {

        Task task = new Task();
        task.setNome(dto.nome());
        task.setStatus(dto.status());
        task.setDataConclusao(dto.dataConclusao());
        task.setDescricao(dto.descricao());

        Task taskSalva = taskRepo.save(task);
        return new TaskResponseDTO(taskSalva);
    }

    public List<TaskResponseDTO> createTaskList(List<TaskCreateDTO> listDTOs) {

        List<Task> tasks = listDTOs.stream().map(dto -> {
            Task t = new Task();
            t.setDataConclusao(dto.dataConclusao());
            t.setStatus(dto.status());
            t.setDescricao(dto.descricao());
            t.setNome(dto.nome());
            return t;
        }).toList();
        List<Task> taskList = taskRepo.saveAll(tasks);

        return taskList.stream()
                .map(TaskResponseDTO::new)
                .toList();
    }

    public TaskResponseDTO getTaskById(Long id) {
        Task task = taskRepo.findById(id)
                .orElseThrow(TaskNotFoundException::new);

        return new TaskResponseDTO(task);
    }

    public List<TaskResponseDTO> getTaskByStatus(Status status) {

        List<Task> tasksList = taskRepo.findAll();

        List<Task> tasksFiltradas = tasksList.stream()
                .filter(task -> task.getStatus() == status)
                .toList();

        if (tasksFiltradas.isEmpty()) {
            throw new TaskNotFoundException("Não existem TASKS com esse STATUS.");
        }

        return tasksFiltradas
                .stream()
                .map(TaskResponseDTO::new)
                .toList();
    }

    public List<TaskResponseDTO> getAllTasks() {

        List<Task> listaTasks = taskRepo.findAll();

        if (listaTasks.isEmpty()) {
            throw new TaskNotFoundException("Não existem tarefas registradas.");
        }

        return listaTasks.stream()
                .map(TaskResponseDTO::new).toList();
    }

    @Transactional
    public TaskResponseDTO putTaskById(Long id, TaskUpdateDTO dto) {

        Task oldTask = taskRepo.findById(id).map(t -> {
                    t.setDataConclusao(dto.dataConclusao());
                    t.setStatus(dto.status());
                    t.setDescricao(dto.descricao());
                    t.setNome(dto.nome());
                    return t;
                })
                .orElseThrow(() -> (new TaskNotFoundException("tarefa não localizada.")));

        Task taskSalva = taskRepo.save(oldTask);
        return new TaskResponseDTO(taskSalva);
    }

    @Transactional
    public void deleteTaskById(Long id) {

        Task oldTask = taskRepo.findById(id)
                .orElseThrow(() -> (new TaskNotFoundException("tarefa não localizada.")));

        taskRepo.deleteById(id);
    }
}
