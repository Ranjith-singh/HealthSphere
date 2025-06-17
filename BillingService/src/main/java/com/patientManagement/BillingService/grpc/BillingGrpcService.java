package com.patientManagement.BillingService.grpc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import billing.BillingRequest;
import billing.BillingResponse;
import billing.BillingServiceGrpc.BillingServiceImplBase;
import io.grpc.stub.StreamObserver;
import net.devh.boot.grpc.server.service.GrpcService;

@GrpcService
public class BillingGrpcService extends BillingServiceImplBase{

    Logger logger = LoggerFactory.getLogger(BillingGrpcService.class);
    @Override
    public void createBillingAccount(BillingRequest billingRequest, StreamObserver<BillingResponse> responseObserver){
        logger.info("createBillingAccount request received {}\n",billingRequest.toString());
        // Business logic
        BillingResponse billingResponse = BillingResponse.newBuilder()
        .setAccountId("12345")
        .setStatus("ACTIVE")
        .build();

        responseObserver.onNext(billingResponse);
        responseObserver.onCompleted();
    }
}
