package com.elderbyte.josc.driver.aws;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.internal.AWSS3V4Signer;
import com.elderbyte.josc.api.JoscConnectionProperties;
import com.elderbyte.josc.api.JoscDriver;
import com.elderbyte.josc.api.ObjectStoreClient;
import com.elderbyte.josc.api.ObjectStoreConnectionException;

public class JoscDriverAwsS3 implements JoscDriver {

    @Override
    public ObjectStoreClient openConnection(String host, JoscConnectionProperties properties) throws ObjectStoreConnectionException {
        try {

            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setSignerOverride("AWSS3V4SignerType");

            AmazonS3 awsS3 = AmazonS3ClientBuilder
                    .standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(host, Regions.US_EAST_1.name()))
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(
                            properties.getRequiredProperty("user"),
                            properties.getRequiredProperty("pass")
                    )))
                    .withChunkedEncodingDisabled(true)
                    .withClientConfiguration(clientConfiguration)
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
