package com.patientManagement.patientService.errorHandler;

public class PatientNotExists extends RuntimeException{
    public PatientNotExists(String message){
        super(message);
    }
}
