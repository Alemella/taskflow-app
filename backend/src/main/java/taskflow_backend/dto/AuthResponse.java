package taskflow_backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponse {

    private String token;
    private Long userId;
    private String name;
    private String email;
    private String role;
}