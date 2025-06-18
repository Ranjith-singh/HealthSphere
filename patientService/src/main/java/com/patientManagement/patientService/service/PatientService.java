package com.patientManagement.patientService.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.patientManagement.patientService.dtos.PatientRequestDto;
import com.patientManagement.patientService.dtos.PatientResponseDto;
import com.patientManagement.patientService.errorHandler.EmailAlreadyExistsException;
import com.patientManagement.patientService.errorHandler.PatientNotExists;
import com.patientManagement.patientService.grpc.BillingGrpcServiceClient;
import com.patientManagement.patientService.mapper.PatientMapper;
import com.patientManagement.patientService.models.Patient;
import com.patientManagement.patientService.repository.PatientRepository;

@Service
public class PatientService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private BillingGrpcServiceClient billingGrpcServiceClient;

    public List<PatientResponseDto> getPatients() {
        List<Patient> patients = patientRepository.findAll();
        return patients.stream().map(patient -> PatientMapper.toDto(patient)).toList();
    }

    public PatientResponseDto createPatient(PatientRequestDto patientRequestDto){
        if (patientRepository.existsByEmail(patientRequestDto.getEmail())) {
            throw new EmailAlreadyExistsException("Email already exists: " + patientRequestDto.getEmail());
        }
        Patient patient = PatientMapper.topatient(patientRequestDto);
        PatientResponseDto patientResponseDto = PatientMapper.toDto(patientRepository.save(patient));
        billingGrpcServiceClient.createBillingAccount(patientResponseDto.getId().toString(),
        patientResponseDto.getUsername(),
        patientResponseDto.getEmail());
        return patientResponseDto;
    }

    public PatientResponseDto updatePatient(UUID id,PatientRequestDto patientRequestDto){
        if(!patientRepository.existsById(id)){
            throw new PatientNotExists("patient doesn't exists: "+ id);
        }
        // System.out.println("value :"+patientRepository.existsByEmailAndIdNot(patientRequestDto.getEmail(), id));
        if(patientRepository.existsByEmailAndIdNot(patientRequestDto.getEmail(), id)){
            throw new EmailAlreadyExistsException("Email Already exists" + patientRequestDto.getEmail());
        }
        Patient patient = patientRepository.getById(id);

        patient.setEmail(patientRequestDto.getEmail());
        patient.setUsername(patientRequestDto.getUsername());
        patient.setAddress(patientRequestDto.getAddress());
        patient.setDateOfBirth(LocalDate.parse(patientRequestDto.getDateOfBirth()));

        PatientResponseDto patientResponseDto = PatientMapper.toDto(patientRepository.save(patient));
        return patientResponseDto;
    }

    public PatientResponseDto deletePatient(UUID id){
        if(!patientRepository.existsById(id)){
            throw new PatientNotExists("Patient doesn't exists"+id);
        }
        PatientResponseDto patientResponseDto = PatientMapper.toDto(patientRepository.getById(id));
        patientRepository.deleteById(id);
        return patientResponseDto;
    }
}
