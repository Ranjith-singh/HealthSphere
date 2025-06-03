package com.patientManagement.patientService.mapper;

import java.time.LocalDate;

import com.patientManagement.patientService.dtos.PatientRequestDto;
import com.patientManagement.patientService.dtos.PatientResponseDto;
import com.patientManagement.patientService.models.Patient;

public class PatientMapper {
    public static PatientResponseDto toDto(Patient patient){
        PatientResponseDto patientResponseDto = new PatientResponseDto();

        patientResponseDto.setId(patient.getId());
        patientResponseDto.setUsername(patient.getUsername());
        patientResponseDto.setEmail(patient.getEmail());
        patientResponseDto.setAddress(patient.getAddress());
        patientResponseDto.setDateOfBirth(patient.getDateOfBirth());
        return patientResponseDto;
    }

    public static Patient topatient(PatientRequestDto patientRequestDto){
        Patient patient = new Patient();
        patient.setUsername(patientRequestDto.getUsername());
        patient.setEmail(patientRequestDto.getEmail());
        patient.setAddress(patientRequestDto.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDto.getDateOfBirth()));
        patient.setRegisteredDate(LocalDate.parse(patientRequestDto.getRegisteredDate()));
        return patient;
    }
}
