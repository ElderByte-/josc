package com.elderbyte.josc.driver.aws;


import com.elderbyte.josc.api.JoscConnectionProperties;
import com.elderbyte.josc.api.JoscDriver;
import com.elderbyte.josc.api.ObjectStoreClient;
import com.elderbyte.josc.api.ObjectStoreConnectionException;
import io.minio.MinioClient;
import software.amazon.awssdk.core.auth.AwsCredentials;
import software.amazon.awssdk.core.auth.AwsCredentialsProvider;
import software.amazon.awssdk.core.auth.StaticCredentialsProvider;
import software.amazon.awssdk.core.regions.Region;
import software.amazon.awssdk.services.s3.S3AdvancedConfiguration;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;

import java.net.URI;

public class JoscDriverAwsS3 implements JoscDriver {

    @Override
    public ObjectStoreClient openConnection(String host, JoscConnectionProperties properties) throws ObjectStoreConnectionException {
        try {


            MinioClient minioClient = new MinioClient(
                    host,
                    properties.getRequiredProperty("user"),
                    properties.getRequiredProperty("pass")
            );


            S3Client s3Client = S3Client.builder()
                    .region(Region.US_EAST_1)
                    .endpointOverride(new URI(host))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsCredentials.create(properties.getRequiredProperty("user"), properties.getRequiredProperty("pass"))))
                    .advancedConfiguration(S3AdvancedConfiguration.builder()
                            .pathStyleAccessEnabled(true)
                            .build())
                    .build();
            /*

            ClientConfiguration clientConfiguration = new ClientConfiguration();
            clientConfiguration.setSignerOverride("AWSS3V4SignerType");

            AmazonS3 awsS3 = AmazonS3ClientBuilder
                    .standard()
                    .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(host, "us-east-1"))
                    .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(
                            properties.getRequiredProperty("user"),
                            properties.getRequiredProperty("pass")
                    )))
                    .withChunkedEncodingDisabled(true)
                    .withClientConfiguration(clientConfiguration)
                    .withPathStyleAccessEnabled(true)
                    .build();


            */
            return new AwsS3ObjectStoreClient(s3Client, minioClient);
        }catch (Exception e){
            throw new ObjectStoreConnectionException("Failed to open connection to minio/S3 object store!", e);
        }
    }

    @Override
    public boolean supports(String protocol, String host, JoscConnectionProperties properties) {
        return "s3".equalsIgnoreCase(protocol) || "aws".equalsIgnoreCase(protocol);
    }

}
