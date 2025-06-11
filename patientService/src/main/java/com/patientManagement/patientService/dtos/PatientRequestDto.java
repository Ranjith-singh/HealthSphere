package com.patientManagement.patientService.dtos;

import com.patientManagement.patientService.dtos.validation.CreatePatient;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PatientRequestDto {
    @NotBlank
    private String username;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String address;

    @NotBlank
    private String dateOfBirth;

    @NotBlank(groups = CreatePatient.class)
    private String registeredDate;

    private String dischargedDate;
}
