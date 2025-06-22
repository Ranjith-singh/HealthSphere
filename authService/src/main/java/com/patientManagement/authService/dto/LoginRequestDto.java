package com.patientManagement.authService.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class LoginRequestDto {
    @NotBlank(message = "The email shouldn't be blank")
    @Email(message = "should be a valid email")
    private String email;

    @NotBlank(message = "the password shouldn't be blank")
    @Size(min= 8, message = "the password should have minimum 8 characters")
    private String password;
}
