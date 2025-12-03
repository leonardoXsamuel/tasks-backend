package br.ce.wcaquino.taskbackend.controller;
import java.util.List;
import br.ce.wcaquino.taskbackend.dto.TaskCreateDTO;
import br.ce.wcaquino.taskbackend.dto.TaskResponseDTO;
import br.ce.wcaquino.taskbackend.dto.TaskUpdateDTO;
import br.ce.wcaquino.taskbackend.model.Status;
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
        public List<TaskResponseDTO> getAllTasks() {
            return taskService.getAllTasks();
        }

    @GetMapping("/id/{id}")
    public TaskResponseDTO getTaskById(@Valid @PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @GetMapping("/status")
    public List<TaskResponseDTO> getTaskByStatus(@Valid @RequestParam Status status) {
        return taskService.getTaskByStatus(status);
    }

    @PostMapping
    public TaskResponseDTO createTask (@Valid @RequestBody TaskCreateDTO task) {
        return taskService.createTask(task);
    }

    @PostMapping("/list")
    public List<TaskResponseDTO> createTaskList(@Valid @RequestBody List<TaskCreateDTO> tasks) {
        return taskService.createTaskList(tasks);
    }

    @PutMapping("/{id}")
    public TaskResponseDTO putTaskById(@PathVariable Long id, @Valid @RequestBody TaskUpdateDTO task){
        return taskService.putTaskById(id, task);
    }

    @DeleteMapping("/{id}")
    public void deleteTaskById(@PathVariable Long id){
        taskService.deleteTaskById(id);
    }

}
