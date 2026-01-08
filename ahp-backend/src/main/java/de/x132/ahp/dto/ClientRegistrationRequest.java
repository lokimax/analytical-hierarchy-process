package de.x132.ahp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRegistrationRequest {

  @NotBlank(message = "Nickname is required")
  @Size(min = 3, max = 32, message = "Nickname must be between 3 and 32 characters")
  private String nickname;

  @NotBlank(message = "Name is required")
  @Size(max = 32, message = "Name must not exceed 32 characters")
  private String name;

  @NotBlank(message = "Surename is required")
  @Size(max = 32, message = "Surename must not exceed 32 characters")
  private String surename;

  @NotBlank(message = "Email is required")
  @Email(message = "Email must be valid")
  @Size(max = 120, message = "Email must not exceed 120 characters")
  private String email;

  @NotBlank(message = "Password is required")
  @Size(min = 8, message = "Password must be at least 8 characters")
  private String password;
}
