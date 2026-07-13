package taskflow_backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import taskflow_backend.entity.Task;
import taskflow_backend.entity.User;
import taskflow_backend.repository.TaskRepository;
import taskflow_backend.repository.UserRepository;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private String getAuthenticatedEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getName())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado");
        }

        return authentication.getName();
    }

    @PostMapping
    public Task createTask(@RequestBody Task task) {
        String email = getAuthenticatedEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no autenticado"));

        // Fuerza propiedad de la tarea al usuario del token
        task.setId(null);
        task.setUser(user);

        return taskRepository.save(task);
    }

    @GetMapping
    public List<Task> getAllTasks() {
        String email = getAuthenticatedEmail();
        return taskRepository.findByUserEmail(email);
    }

    @GetMapping("/{id}")
    public Task getTaskById(@PathVariable Long id) {
        String email = getAuthenticatedEmail();

        return taskRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarea no encontrada"));
    }

    @DeleteMapping("/{id}")
    public String deleteTask(@PathVariable Long id) {
        String email = getAuthenticatedEmail();

        Task task = taskRepository.findByIdAndUserEmail(id, email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarea no encontrada"));

        taskRepository.delete(task);
        return "Tarea eliminada correctamente";
    }

    @PutMapping("/{id}")
    public Task updateTask(@PathVariable Long id, @RequestBody Task updatedTask) {
        String email = getAuthenticatedEmail();

        return taskRepository.findByIdAndUserEmail(id, email)
                .map(task -> {
                    task.setTitle(updatedTask.getTitle());
                    task.setDescription(updatedTask.getDescription());
                    task.setPriority(updatedTask.getPriority());
                    task.setStatus(updatedTask.getStatus());
                    task.setDueDate(updatedTask.getDueDate());
                    return taskRepository.save(task);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarea no encontrada"));
    }

    @PatchMapping("/{id}/complete")
    public Task completeTask(@PathVariable Long id) {
        String email = getAuthenticatedEmail();

        return taskRepository.findByIdAndUserEmail(id, email)
                .map(task -> {
                    task.setStatus("COMPLETED");
                    return taskRepository.save(task);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Tarea no encontrada"));
    }
}