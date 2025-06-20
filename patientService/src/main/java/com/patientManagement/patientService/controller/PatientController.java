package com.patientManagement.patientService.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.patientManagement.patientService.dtos.PatientRequestDto;
import com.patientManagement.patientService.dtos.PatientResponseDto;
import com.patientManagement.patientService.dtos.validation.CreatePatient;
import com.patientManagement.patientService.service.PatientService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
// import jakarta.validation.Valid;
import jakarta.validation.groups.Default;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/patient")
@Tag(name = "patient", description = "Api's for managing patients")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @GetMapping()
    @Operation(summary = "get patients")
    public ResponseEntity<List<PatientResponseDto>> getPatients() {
        List<PatientResponseDto> patients = patientService.getPatients();
        return ResponseEntity.ok().body(patients);
    }

    @PostMapping()
    @Operation(summary = "create patients")
    public ResponseEntity<PatientResponseDto> createPatient(
        @Validated({Default.class, CreatePatient.class}) @RequestBody PatientRequestDto patientRequestDto) {
        PatientResponseDto patientResponseDto = patientService.createPatient(patientRequestDto);
        return ResponseEntity.ok().body(patientResponseDto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "update patients")
    public ResponseEntity<PatientResponseDto> updatePatient(@PathVariable UUID id, 
    @Validated({Default.class}) @RequestBody PatientRequestDto patientRequestDto) {
        PatientResponseDto patientResponseDto = patientService.updatePatient(id, patientRequestDto);
        return ResponseEntity.ok().body(patientResponseDto);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "delete patients")
    public ResponseEntity<PatientResponseDto> deletePatient(@PathVariable UUID id){
        PatientResponseDto patientResponseDto = patientService.deletePatient(id);
        return ResponseEntity.ok().body(patientResponseDto);
    }
}
