package taskflow_backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import taskflow_backend.repository.TaskRepository;
import taskflow_backend.repository.UserRepository;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void createUser_whenAdminCreatesUser_forcesDefaultRoleUser() throws Exception {
        String payload = """
                {
                  "name": "Nuevo usuario",
                  "email": "nuevo@test.com",
                  "password": "123456"
                }
                """;

        mockMvc.perform(post("/users")
                .with(SecurityMockMvcRequestPostProcessors.user("admin@test.com").roles("ADMIN"))
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Nuevo usuario"))
                .andExpect(jsonPath("$.email").value("nuevo@test.com"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.password").doesNotExist());
    }

    @Test
    void getAllUsers_whenCallerIsNotAdmin_returnsForbidden() throws Exception {
        mockMvc.perform(get("/users")
                .with(SecurityMockMvcRequestPostProcessors.user("user@test.com").roles("USER")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_whenAdminReturnsUsersWithoutPassword() throws Exception {
        userRepository.save(buildUser("uno@test.com", "Uno"));

        mockMvc.perform(get("/users")
                .with(SecurityMockMvcRequestPostProcessors.user("admin@test.com").roles("ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].email").value("uno@test.com"))
                .andExpect(jsonPath("$[0].role").value("USER"))
                .andExpect(jsonPath("$[0].password").doesNotExist());
    }

    private taskflow_backend.entity.User buildUser(String email, String name) {
        taskflow_backend.entity.User user = new taskflow_backend.entity.User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword("encoded-password");
        user.setRole("USER");
        return user;
    }
}