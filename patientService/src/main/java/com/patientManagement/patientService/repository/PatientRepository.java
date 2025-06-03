package com.patientManagement.patientService.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.patientManagement.patientService.models.Patient;

@Repository
public interface PatientRepository extends JpaRepository<Patient,UUID>{

}
