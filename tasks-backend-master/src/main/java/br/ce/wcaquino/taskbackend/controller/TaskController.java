package br.ce.wcaquino.taskbackend.controller;

import java.util.List;

import br.ce.wcaquino.taskbackend.dto.TaskCreateDTO;
import br.ce.wcaquino.taskbackend.dto.TaskResponseDTO;
import br.ce.wcaquino.taskbackend.dto.TaskUpdateDTO;
import br.ce.wcaquino.taskbackend.model.Status;
import br.ce.wcaquino.taskbackend.service.TaskService;
import jakarta.validation.Valid;
import jdk.incubator.vector.VectorOperators;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<List<TaskResponseDTO>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    @GetMapping("/id/{id}")
    public ResponseEntity<TaskResponseDTO> getTaskById(@Valid @PathVariable Long id) {
        return ResponseEntity.ok(taskService.getTaskById(id));
    }

    @GetMapping("/status")
    public ResponseEntity<List<TaskResponseDTO>> getTaskByStatus(@Valid @RequestParam Status status) {
        return ResponseEntity.ok(taskService.getTaskByStatus(status));
    }

    @PostMapping
    public ResponseEntity<TaskResponseDTO> createTask(@Valid @RequestBody TaskCreateDTO task) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(task));
    }

    @PostMapping("/list")
    public ResponseEntity<List<TaskResponseDTO>> createTaskList(@Valid @RequestBody List<TaskCreateDTO> tasks) {
        return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTaskList(tasks));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TaskResponseDTO> putTaskById(@PathVariable Long id, @Valid @RequestBody TaskUpdateDTO task) {
        return ResponseEntity.ok(taskService.putTaskById(id, task));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long id) {
        taskService.deleteTaskById(id);
        return ResponseEntity.noContent().build();
    }

}
