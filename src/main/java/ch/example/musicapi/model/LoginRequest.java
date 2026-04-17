package ch.example.musicapi.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {

  @NotNull(message = "Username must not be null")
  private String username;

  @NotNull(message = "Password must not be null")
  private String password;
}
