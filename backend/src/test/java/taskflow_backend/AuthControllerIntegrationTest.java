package taskflow_backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import taskflow_backend.entity.User;
import taskflow_backend.repository.UserRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void register_whenPayloadIsValid_returnsCreatedAndToken() throws Exception {
        String payload = """
                {
                  \"name\": \"Alejandro\",
                  \"email\": \"alejandro@test.com\",
                  \"password\": \"123456\"
                }
                """;

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.userId").isNumber())
                .andExpect(jsonPath("$.name").value("Alejandro"))
                .andExpect(jsonPath("$.email").value("alejandro@test.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    @Test
    void register_whenEmailAlreadyExists_returnsConflict() throws Exception {
        String payload = """
                {
                  \"name\": \"Alejandro\",
                  \"email\": \"alejandro@test.com\",
                  \"password\": \"123456\"
                }
                """;

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("El email ya esta registrado"));
    }

    @Test
    void login_whenCredentialsAreValid_returnsOkAndToken() throws Exception {
        String registerPayload = """
                {
                  \"name\": \"Alejandro\",
                  \"email\": \"alejandro@test.com\",
                  \"password\": \"123456\"
                }
                """;

        String loginPayload = """
                {
                  \"email\": \"alejandro@test.com\",
                  \"password\": \"123456\"
                }
                """;

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(registerPayload))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginPayload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString())
                .andExpect(jsonPath("$.email").value("alejandro@test.com"));
    }

    @Test
    void login_whenPasswordIsWrong_returnsUnauthorized() throws Exception {
        User user = new User();
        user.setName("Alejandro");
        user.setEmail("alejandro@test.com");
        user.setPassword("$2a$10$WtlD8jH2P8eab4hAho5P4uA.T4CrxN6W2aP2M5yqJDb2I6m6m0K8C");
        user.setRole("USER");
        userRepository.save(user);

        String loginPayload = """
                {
                  \"email\": \"alejandro@test.com\",
                  \"password\": \"incorrecta\"
                }
                """;

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginPayload))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Credenciales invalidas"));
    }

    @Test
    void register_whenPayloadIsInvalid_returnsBadRequestWithValidationErrors() throws Exception {
        String payload = """
                {
                  \"name\": \"\",
                  \"email\": \"correo-no-valido\",
                  \"password\": \"123\"
                }
                """;

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Error de validacion"))
                .andExpect(jsonPath("$.validationErrors.name").exists())
                .andExpect(jsonPath("$.validationErrors.email").exists())
                .andExpect(jsonPath("$.validationErrors.password").exists());
    }
}
