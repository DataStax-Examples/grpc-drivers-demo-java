package com.datastax.samples.grpc;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.stargate.grpc.StargateBearerToken;
import io.stargate.proto.QueryOuterClass;
import io.stargate.proto.QueryOuterClass.Response;
import io.stargate.proto.StargateGrpc;

/**
 * Connect to Astra through Grpc
 *
 * @author Cedrick LUNVEN (@clunven)
 */
public class GrpcAstra101 {
    
    // Settings
    public static final String ASTRA_DB_TOKEN  = "<change_me>";
    public static final String ASTRA_DB_ID     = "<change_me>";
    public static final String ASTRA_DB_REGION = "<change_me>";
    
    public static void main(String[] args) {
        
        // Open Grpc communicatino 
        ManagedChannel channel = ManagedChannelBuilder
            .forAddress(ASTRA_DB_ID + "-" + ASTRA_DB_REGION + ".apps.astra.datastax.com", 443)
            .useTransportSecurity()
            .build();
        
        // use Grpc Stub generated from .proto as a client
        StargateGrpc.StargateBlockingStub cloudNativeClient = StargateGrpc
                .newBlockingStub(channel)
                .withCallCredentials(new StargateBearerToken(ASTRA_DB_TOKEN))
                .withDeadlineAfter(5, TimeUnit.SECONDS);
        
        // create Query
        String cqlQuery = "SELECT data_center from system.local";
        
        // Execute the Query
        Response res = cloudNativeClient.executeQuery(QueryOuterClass
                        .Query.newBuilder().setCql(cqlQuery).build());

        // Accessing Row result
        QueryOuterClass.Row row = res.getResultSet().getRowsList().get(0);
        
        // Access the single value
        String datacenterName = row.getValues(0).getString();
        System.out.println("You are connected to '%s'".formatted(datacenterName));
        
        // Validating the test
        Assertions.assertNotNull(datacenterName);
        
    }
        

}
