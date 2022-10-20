package com.datastax.samples.grpc;

import org.junit.jupiter.api.Assertions;

import com.datastax.astra.sdk.AstraClient;
import com.datastax.stargate.sdk.grpc.ApiGrpcClient;
import com.datastax.stargate.sdk.grpc.domain.ResultSetGrpc;

public class GrpcAstraSdk {
    
    // Settings
    public static final String ASTRA_DB_TOKEN  = "<change_me>";
    public static final String ASTRA_DB_ID     = "<change_me>";
    public static final String ASTRA_DB_REGION = "<change_me>";
    
    public static void main(String[] args) {
        
        // Initialize Astra Client with token and database identifiers
        try(AstraClient astraClient = AstraClient.builder()
                .withDatabaseId(ASTRA_DB_ID)
                .withDatabaseRegion(ASTRA_DB_REGION)
                .withToken(ASTRA_DB_TOKEN)
                .enableGrpc()
                .build()) {
            
            // Accessin the gRPC API
            ApiGrpcClient cloudNativeClient = astraClient.apiStargateGrpc();
            
            // Reuse cql query
            String cqlQuery = "SELECT data_center from system.local";
            
            // Executing Query
            ResultSetGrpc rs = cloudNativeClient.execute(cqlQuery);
            
            // Accessing reulst
            String datacenterName = rs.one().getString("data_center");
            System.out.println("You are connected to '%s'".formatted(datacenterName));
            
            // Validating the test
            Assertions.assertNotNull(datacenterName);
        }
    }  

}
