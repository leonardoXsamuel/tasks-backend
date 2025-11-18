package br.ce.wcaquino.taskbackend.service;

import br.ce.wcaquino.taskbackend.advice.exceptions.TaskNotFoundException;
import br.ce.wcaquino.taskbackend.model.Task;
import br.ce.wcaquino.taskbackend.repo.TaskRepo;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepo taskRepo;

    public TaskService(TaskRepo taskRepo) {
        this.taskRepo = taskRepo;
    }

    public Task createTask(Task task) {
        return taskRepo.save(task);
    }

    public List<Task> createTaskList(List<Task> task) {
        return taskRepo.saveAll(task);
    }

    public Optional<Task> getTaskById(Long id) {

        if (!taskRepo.existsById(id)) {
            throw new TaskNotFoundException();
        }

        return taskRepo.findById(id);
    }

    public List<Task> getAllTasks() {

        if (taskRepo.findAll().isEmpty()) {
            throw new TaskNotFoundException("Não existem tarefas registradas no banco de dados.");
        }
        return taskRepo.findAll();
    }

    @Transactional
    public Task putTaskById(Long id, Task newTask) {

        Task oldTask = taskRepo.findById(id)
                .orElseThrow(() -> (new TaskNotFoundException("tarefa não localizada.")));

        oldTask.setNome(newTask.getNome());
        oldTask.setDescricao(newTask.getDescricao());
        oldTask.setData(newTask.getData());

        return taskRepo.save(oldTask);
    }

    @Transactional
    public void deleteTaskById(Long id) {

        Task oldTask = taskRepo.findById(id)
                .orElseThrow(() -> (new TaskNotFoundException("tarefa não localizada.")));

        taskRepo.deleteById(id);
    }
}
