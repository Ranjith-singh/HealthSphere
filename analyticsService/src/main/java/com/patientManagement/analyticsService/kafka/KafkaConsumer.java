package com.patientManagement.analyticsService.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.google.protobuf.InvalidProtocolBufferException;

import patient.events.PatientEvent;

@Service
public class KafkaConsumer {

    Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = "patient",groupId = "analyticsService")
    public void consumeEvent(byte[] event){
        try {
            PatientEvent patientEvent = PatientEvent.parseFrom(event);
            logger.info("Deserialized patientEvent patientId: {}, username: {}, email:{}, event_type:{}",
                        patientEvent.getPatientId(),
                        patientEvent.getUsername(),
                        patientEvent.getEmail(),
                        patientEvent.getEventType());
        } catch (InvalidProtocolBufferException e) {
            logger.error("Error Deserializing event {}", e.getMessage());
        }
    }

}
