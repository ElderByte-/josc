package com.elderbyte.josc.driver.aws;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.elderbyte.josc.api.BlobObject;
import com.elderbyte.josc.api.Bucket;
import com.elderbyte.josc.api.ObjectStoreClient;

import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AwsS3ObjectStoreClient implements ObjectStoreClient {

    private final AmazonS3 s3client;


    public AwsS3ObjectStoreClient(AmazonS3 s3client){
        if(s3client == null) throw new IllegalArgumentException("s3client must not be null!");
        this.s3client = s3client;
    }



    @Override
    public Stream<Bucket> listBuckets() {
        return s3client.listBuckets().stream()
                .map(b -> AwsBlobObjectBuilder.build(b));
    }

    @Override
    public boolean bucketExists(String bucket) {
        return s3client.doesBucketExistV2(bucket);
    }

    @Override
    public void removeBucket(String bucket) {
        s3client.deleteBucket(bucket);
    }

    @Override
    public void createBucket(String bucket) {
        s3client.createBucket(bucket);
    }

    @Override
    public Stream<BlobObject> listBlobObjects(String bucket) {
        return listBlobObjects(bucket, null);
    }

    @Override
    public Stream<BlobObject> listBlobObjects(String bucket, String keyPrefix) {
        return listBlobObjects(bucket, keyPrefix, true);
    }

    @Override
    public Stream<BlobObject> listBlobObjects(String bucket, String keyPrefix, boolean recursive) {

        ListObjectsV2Result result = s3client.listObjectsV2(new ListObjectsV2Request()
                .withBucketName(bucket)
                .withPrefix(keyPrefix)
                .withDelimiter(recursive ? null : "/")
        );

        List<BlobObject> objectList = result.getObjectSummaries().stream()
                .map(s -> AwsBlobObjectBuilder.build(s))
                .collect(Collectors.toList());

        List<BlobObject> directories = result.getCommonPrefixes().stream()
                .map(p -> AwsBlobObjectBuilder.buildDirectory(p))
                .collect(Collectors.toList());

        directories.addAll(objectList);

        return directories.stream();
    }

    @Override
    public BlobObject getBlobObjectInfo(String bucket, String key) {
        ObjectMetadata meta = s3client.getObjectMetadata(bucket, key);
        return AwsBlobObjectBuilder.build(key, meta);
    }

    public boolean objectExists(String bucket, String key){
        return s3client.doesObjectExist(bucket, key);
    }

    @Override
    public InputStream getBlobObject(String bucket, String key) {
        return s3client.getObject(bucket, key).getObjectContent();
    }

    @Override
    public InputStream getBlobObject(String bucket, String key, long offset) {
        return s3client.getObject(new GetObjectRequest(bucket, key).withRange(offset)).getObjectContent();
    }

    @Override
    public void putBlobObject(String bucket, String key, Path file) {
        s3client.putObject(bucket, key, file.toFile());
    }

    @Override
    public void putBlobObject(String bucket, String key, InputStream objectStream, long length, String mimeType) {
        s3client.putObject(bucket, key, objectStream, new ObjectMetadata());
    }

    @Override
    public void deleteBlobObject(String bucket, String key) {
        s3client.deleteObject(bucket, key);
    }

    @Override
    public void copyBlobObject(String sourceBucket, String sourceKey, String destinationBucket, String destinationKey) {
        s3client.copyObject(sourceBucket, sourceKey, destinationBucket, destinationKey);
    }

    @Override
    public String getTempGETUrl(String bucket, String key) {
        return s3client.generatePresignedUrl(bucket, key, expirationDate(), HttpMethod.GET).toString();
    }

    @Override
    public String getTempPUTUrl(String bucket, String key) {
        return s3client.generatePresignedUrl(bucket, key, expirationDate(), HttpMethod.PUT).toString();
    }

    @Override
    public String getPublicUrl(String bucket, String key) {
        return s3client.getUrl(bucket, key).toString();
    }

    private Date expirationDate(){
        LocalDateTime now = LocalDateTime.now().plus(1, ChronoUnit.DAYS);
        return Date.from(now.toInstant(ZoneOffset.UTC));
    }
}
