package com.patientManagement.patientService.dtos;

import java.time.LocalDate;
import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PatientResponseDto {
    @NotNull
    private UUID id;

    @NotNull
    private String username;

    @NotNull
    private String email;

    @NotNull
    private String address;

    @NotNull
    private LocalDate dateOfBirth;
}
