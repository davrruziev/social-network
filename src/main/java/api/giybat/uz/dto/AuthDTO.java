package api.giybat.uz.dto;

import jakarta.validation.constraints.NotBlank;

public class AuthDTO {
    @NotBlank(message = "UserName required")
    private String username;
    @NotBlank(message = "Password required")
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
