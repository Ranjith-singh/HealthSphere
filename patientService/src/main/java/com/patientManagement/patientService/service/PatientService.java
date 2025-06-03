package com.patientManagement.patientService.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.patientManagement.patientService.dtos.PatientRequestDto;
import com.patientManagement.patientService.dtos.PatientResponseDto;
import com.patientManagement.patientService.mapper.PatientMapper;
import com.patientManagement.patientService.models.Patient;
import com.patientManagement.patientService.repository.PatientRepository;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    public List<PatientResponseDto> getPatients(){
        List<Patient> patients = patientRepository.findAll();
        return patients.stream().map(patient -> PatientMapper.toDto(patient)).toList();
    }

    public PatientResponseDto createPatient(PatientRequestDto patientRequestDto){
        Patient patient = PatientMapper.topatient(patientRequestDto);
        PatientResponseDto patientResponseDto = PatientMapper.toDto(patientRepository.save(patient));
        return patientResponseDto;
    }
}
