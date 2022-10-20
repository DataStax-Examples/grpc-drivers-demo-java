package com.datastax.samples.grpc;

import org.junit.jupiter.api.Assertions;

import com.datastax.stargate.sdk.StargateClient;
import com.datastax.stargate.sdk.config.StargateNodeConfig;
import com.datastax.stargate.sdk.grpc.domain.ResultSetGrpc;

public class GrpcStargateSdk {
    
    private static final String STARGATE_USERNAME  = "cassandra";
    private static final String STARGATE_PASSWORD  = "cassandra";
    private static final String STARGATE_HOST      = "localhost";
    
    public static void main(String[] args) {
        
        try(StargateClient stargateClient = StargateClient.builder()
            .withAuthCredentials(STARGATE_USERNAME, STARGATE_PASSWORD)
            .withLocalDatacenter("datacenter1")
            .withApiNode(new StargateNodeConfig(STARGATE_HOST))
            .enableGrpc()
            .build()) {
            
            // Reuse cql query
            String cqlQuery = "SELECT data_center from system.local";
            
            // Executing Query
            ResultSetGrpc rs = stargateClient.apiGrpc().execute(cqlQuery);
            
            // Accessing reulst
            String datacenterName = rs.one().getString("data_center");
            System.out.println("You are connected to '%s'".formatted(datacenterName));
            
            // Validating the test
            Assertions.assertNotNull(datacenterName);
        }
    }

}
