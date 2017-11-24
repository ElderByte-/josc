package com.elderbyte.josc.driver.aws;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.elderbyte.josc.api.JoscConnectionProperties;
import com.elderbyte.josc.api.JoscDriver;
import com.elderbyte.josc.api.ObjectStoreClient;
import com.elderbyte.josc.api.ObjectStoreConnectionException;

public class JoscDriverAwsS3 implements JoscDriver {

    @Override
    public ObjectStoreClient openConnection(String host, JoscConnectionProperties properties) throws ObjectStoreConnectionException {


        try {
            AmazonS3 awsS3 = AmazonS3ClientBuilder.standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(host, "us-east-1"))
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(
                            properties.getRequiredProperty("user"),
                            properties.getRequiredProperty("pass")
                    )))
                    .withPathStyleAccessEnabled(true)
                    .build();

            return new AwsS3ObjectStoreClient(awsS3);
        }catch (Exception e){
            throw new ObjectStoreConnectionException("Failed to open connection to minio/S3 object store!", e);
        }
    }

    @Override
    public boolean supports(String protocol, String host, JoscConnectionProperties properties) {
        return "s3".equalsIgnoreCase(protocol) || "aws".equalsIgnoreCase(protocol);
    }

}
