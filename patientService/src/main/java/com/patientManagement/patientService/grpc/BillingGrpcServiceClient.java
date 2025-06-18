package com.patientManagement.patientService.grpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

@Service
public class BillingGrpcServiceClient {
    Logger logger = LoggerFactory.getLogger(BillingGrpcServiceClient.class);
    private BillingServiceGrpc.BillingServiceBlockingStub billingStub;
    public BillingGrpcServiceClient(
    @Value("${billing.service.address:localhost}") String serverAddress,
    @Value("${billing.service.grpc.port:9001}") int serverPort){
        logger.info("Connecting to billing service via Grpc at {}:{}",serverAddress,serverPort);
        ManagedChannel managedChannel = ManagedChannelBuilder.forAddress(serverAddress,serverPort).usePlaintext().build();
        billingStub = BillingServiceGrpc.newBlockingStub(managedChannel);
    }

    public BillingResponse createBillingAccount(String patientId, String username, String email){
        BillingRequest billingRequest = BillingRequest.newBuilder()
        .setPatientId(patientId)
        .setUsername(username)
        .setEmail(email)
        .build();
        BillingResponse billingResponse = billingStub.createBillingAccount(billingRequest);
        logger.info("Received response from billing service via Grpc:{}",billingResponse);
        return billingResponse;
    }
}
