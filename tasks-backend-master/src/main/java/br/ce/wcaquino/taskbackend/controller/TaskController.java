package br.ce.wcaquino.taskbackend.controller;

import java.util.List;
import java.util.Optional;
import br.ce.wcaquino.taskbackend.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;
import br.ce.wcaquino.taskbackend.model.Task;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getAllTasks();
    }

    @GetMapping("/id/{id}")
    public Optional<Task> getTaskById(@Valid @PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @GetMapping("/status")
    public List<Task> getTaskByStatus(@Valid @RequestParam Task.Status status) {
        return taskService.getTaskByStatus(status);
    }

    @PostMapping
    public Task createTask (@Valid @RequestBody Task task) {
        return taskService.createTask(task);
    }

    @PostMapping("/list")
    public List<Task> createTaskList(@Valid @RequestBody List<Task> tasks) {
        return taskService.createTaskList(tasks);
    }

    @PutMapping("/{id}")
    public Task putTaskById(@PathVariable Long id, @Valid @RequestBody Task task){
        return taskService.putTaskById(id, task);
    }

    @DeleteMapping("/{id}")
    public void deleteTaskById(@PathVariable Long id){
        taskService.deleteTaskById(id);
    }

}
