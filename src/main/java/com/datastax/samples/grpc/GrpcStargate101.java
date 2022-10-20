package com.datastax.samples.grpc;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Assertions;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.stargate.grpc.StargateBearerToken;
import io.stargate.proto.QueryOuterClass;
import io.stargate.proto.QueryOuterClass.Response;
import io.stargate.proto.StargateGrpc;

public class GrpcStargate101 {// Stargate OSS configuration for locally hosted docker image

    private static final String STARGATE_USERNAME  = "cassandra";
    private static final String STARGATE_PASSWORD  = "cassandra";
    private static final String STARGATE_HOST      = "localhost";
    private static final int    STARGATE_GRPC_PORT = 8090;
    private static final String STARGATE_AUTH_ENDPOINT = "http://" + STARGATE_HOST + ":8081/v1/auth";

    public static void main(String[] args) throws Exception {

        // Authenticate to get a token (http client jdk11)
        String token = login(STARGATE_USERNAME, STARGATE_PASSWORD);

        // Open Grpc communication
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(STARGATE_HOST, STARGATE_GRPC_PORT)
                .usePlaintext()
                .build();

        // blocking stub version
        StargateGrpc.StargateBlockingStub blockingStub = StargateGrpc
                .newBlockingStub(channel)
                .withDeadlineAfter(10, TimeUnit.SECONDS)
                .withCallCredentials(new StargateBearerToken(token));

       
        // create Query
        String cqlQuery = "SELECT data_center from system.local";
        
        // Execute the Query
        Response res = blockingStub.executeQuery(QueryOuterClass
                        .Query.newBuilder().setCql(cqlQuery).build());

        // Accessing Row result
        QueryOuterClass.Row row = res.getResultSet().getRowsList().get(0);
        
        // Access the single value
        String datacenterName = row.getValues(0).getString();
        System.out.println("You are connected to '%s'".formatted(datacenterName));
        
        // Validating the test
        Assertions.assertNotNull(datacenterName);
    }
    
    
    private static String login(String username, String password) {
        try {
            HttpRequest request = HttpRequest.newBuilder().GET().uri(URI.create(STARGATE_AUTH_ENDPOINT))
                    .setHeader("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString("{" + " \"username\": \"" + STARGATE_USERNAME + "\",\n"
                            + " \"password\": \"" + STARGATE_PASSWORD + "\"\n" + "}'"))
                    .build();
            HttpResponse<String> response = HttpClient.newBuilder().build().send(request, HttpResponse.BodyHandlers.ofString());
            return response.body().split(":")[1].replaceAll("\"", "").replaceAll("}", "");
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

}
