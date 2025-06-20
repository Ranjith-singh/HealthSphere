package com.patientManagement.patientService.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.patientManagement.patientService.dtos.PatientResponseDto;

import patient.events.PatientEvent;

@Service
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, byte[]> kafkaTemplate;
    Logger logger = LoggerFactory.getLogger(KafkaProducer.class);

    public void sendEvent(PatientResponseDto patientResponseDto){
        PatientEvent patientEvent = PatientEvent.newBuilder()
        .setPatientId(patientResponseDto.getId().toString())
        .setUsername(patientResponseDto.getUsername())
        .setEmail(patientResponseDto.getEmail())
        .setEventType("CREATE_PATIENT")
        .build();

        try {
            kafkaTemplate.send("patient",patientEvent.toByteArray());
        } catch (Exception e) {
            logger.error("Error sending patient created event: ",patientEvent);
        }
    }
}
