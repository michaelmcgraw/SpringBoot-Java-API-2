package com.kenzie.capstone.service.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

public class SendEmailNotification implements RequestHandler<String, String> {

    public String handleRequest(String input, Context context) {

        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();


        String topicArn = "arn:aws:sns:us-east-1:653914639905:NotifyUser";

        String message = "You have been added to a Playlist";

        PublishRequest publishRequest = PublishRequest.builder()
                .topicArn(topicArn)
                .message(message)
                .build();

        try {
            PublishResponse response = snsClient.publish(publishRequest);
            System.out.println("Message sent with message ID: " + response.messageId());
            return "Email sent successfully!";
        } catch (Exception e) {
            System.err.println("Error sending email: " + e.getMessage());
            return "Error sending email: " + e.getMessage();
        }
    }
}
