package taskflow_backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import taskflow_backend.entity.Task;
import taskflow_backend.entity.User;
import taskflow_backend.repository.TaskRepository;
import taskflow_backend.repository.UserRepository;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TaskControllerSecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    private User owner;
    private User otherUser;
    private Task ownerTask;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();

        owner = userRepository.save(buildUser("owner@test.com", "Owner"));
        otherUser = userRepository.save(buildUser("other@test.com", "Other"));

        ownerTask = new Task();
        ownerTask.setTitle("Tarea privada");
        ownerTask.setDescription("Solo owner puede verla");
        ownerTask.setPriority("HIGH");
        ownerTask.setStatus("PENDING");
        ownerTask.setDueDate(LocalDateTime.now().plusDays(2));
        ownerTask.setUser(owner);
        ownerTask = taskRepository.save(ownerTask);

        Task otherTask = new Task();
        otherTask.setTitle("Tarea de otro usuario");
        otherTask.setDescription("No debe aparecer para owner");
        otherTask.setPriority("LOW");
        otherTask.setStatus("PENDING");
        otherTask.setDueDate(LocalDateTime.now().plusDays(5));
        otherTask.setUser(otherUser);
        taskRepository.save(otherTask);
    }

    @Test
    void getTasks_returnsOnlyAuthenticatedUserTasks() throws Exception {
        mockMvc.perform(get("/tasks")
                .with(SecurityMockMvcRequestPostProcessors.user("owner@test.com").roles("USER")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value(ownerTask.getId()))
                .andExpect(jsonPath("$[0].user.email").value("owner@test.com"));
    }

        @Test
        void createTask_whenRequestHasNoAuthentication_returnsUnauthorized() throws Exception {
        String payload = """
            {
              "title": "Sin token",
              "description": "Debe fallar",
              "priority": "LOW",
              "status": "PENDING",
              "dueDate": "2030-01-01T12:00:00"
            }
            """;

        mockMvc.perform(post("/tasks")
            .contentType(MediaType.APPLICATION_JSON)
            .content(payload))
            .andExpect(status().isForbidden());
        }

    @Test
    void getTaskById_whenTaskBelongsToAnotherUser_returnsNotFound() throws Exception {
        mockMvc.perform(get("/tasks/{id}", ownerTask.getId())
                .with(SecurityMockMvcRequestPostProcessors.user("other@test.com").roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteTask_whenTaskBelongsToAnotherUser_returnsNotFound() throws Exception {
        mockMvc.perform(delete("/tasks/{id}", ownerTask.getId())
                .with(SecurityMockMvcRequestPostProcessors.user("other@test.com").roles("USER")))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateTask_whenTaskBelongsToAnotherUser_returnsNotFound() throws Exception {
        String payload = """
                {
                  \"title\": \"Intento de takeover\",
                  \"description\": \"No debería poder actualizar\",
                  \"priority\": \"MEDIUM\",
                  \"status\": \"IN_PROGRESS\",
                  \"dueDate\": \"2030-01-01T12:00:00\"
                }
                """;

        mockMvc.perform(put("/tasks/{id}", ownerTask.getId())
                .with(SecurityMockMvcRequestPostProcessors.user("other@test.com").roles("USER"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isNotFound());
    }

    private User buildUser(String email, String name) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("encoded-password");
        user.setRole("USER");
        return user;
    }
}
