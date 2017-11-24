package com.elderbyte.josc.driver.aws;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.elderbyte.josc.api.*;
import com.elderbyte.josc.api.Bucket;

import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class AwsS3ObjectStoreClient implements ObjectStoreClient {

    private static final int MAX_KEYS = 1000;
    private final AmazonS3 s3client;


    public AwsS3ObjectStoreClient(AmazonS3 s3client){
        if(s3client == null) throw new IllegalArgumentException("s3client must not be null!");
        this.s3client = s3client;
    }


    @Override
    public Stream<Bucket> listBuckets() {
        try {
            return s3client.listBuckets().stream()
                    .map(b -> AwsBlobObjectBuilder.build(b));
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to listBuckets!", e);
        }
    }

    @Override
    public boolean bucketExists(String bucket) {
        validateBucketNameOrThrow(bucket);

        try {
            return s3client.doesBucketExistV2(bucket);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to bucketExists + " + bucket, e);
        }
    }

    @Override
    public void removeBucket(String bucket) {
        validateBucketNameOrThrow(bucket);

        try {
            s3client.deleteBucket(bucket);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to removeBucket + " + bucket, e);
        }
    }

    @Override
    public Bucket createBucket(String bucket) {
        validateBucketNameOrThrow(bucket);

        try {
            return AwsBlobObjectBuilder.build(s3client.createBucket(bucket));
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to removeBucket + " + bucket, e);
        }
    }

    @Override
    public Stream<BlobObject> listBlobObjects(String bucket) {
        validateBucketNameOrThrow(bucket);

        return listBlobObjects(bucket, null);
    }

    @Override
    public Stream<BlobObject> listBlobObjects(String bucket, String keyPrefix) {
        validateBucketNameOrThrow(bucket);

        return listBlobObjects(bucket, keyPrefix, true);
    }

    @Override
    public Stream<BlobObject> listBlobObjects(String bucket, String keyPrefix, boolean recursive) {
        validateBucketNameOrThrow(bucket);

        List<BlobObject> alldata = new ArrayList<>();

        ContinuableListing<BlobObject> listing = listBlobObjectsChunked(bucket, keyPrefix, recursive, MAX_KEYS);
        alldata.addAll(listing.getContent());

        while (listing.hasMore()){
            listing = listBlobObjectsChunked(bucket, keyPrefix, recursive, MAX_KEYS, listing.getNextContinuationToken());
            alldata.addAll(listing.getContent());
        }

        return alldata.stream();
    }

    @Override
    public ContinuableListing<BlobObject> listBlobObjectsChunked(String bucket, String keyPrefix, boolean recursive, int maxKeys, String nextContinuationToken) {

        validateBucketNameOrThrow(bucket);
        if(maxKeys > MAX_KEYS) throw new IllegalArgumentException("maxKeys must be <= " + MAX_KEYS);

        try{
            ListObjectsV2Result result = s3client.listObjectsV2(new ListObjectsV2Request()
                    .withBucketName(bucket)
                    .withPrefix(keyPrefix)
                    .withDelimiter(recursive ? null : "/")
                    .withMaxKeys(maxKeys)
                    .withContinuationToken(nextContinuationToken)
            );

            return AwsBlobObjectBuilder.buildChunk(result);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to listBlobObjectsChunked: + bucket: " + bucket + ", keyPrefix:" + keyPrefix + ", recursive " + recursive + ", maxKeys " + maxKeys + ", next-token " + nextContinuationToken, e);
        }
    }

    @Override
    public BlobObject getBlobObjectInfo(String bucket, String key) {
        validateBucketNameOrThrow(bucket);
        validateKeyOrThrow(key);

        try{
            ObjectMetadata meta = s3client.getObjectMetadata(bucket, key);
            return AwsBlobObjectBuilder.build(key, meta);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to getBlobObjectInfo: + bucket: " + bucket + ", key:" + key, e);
        }
    }

    public boolean objectExists(String bucket, String key){
        validateBucketNameOrThrow(bucket);
        validateKeyOrThrow(key);

        try {
            return s3client.doesObjectExist(bucket, key);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to objectExists: + bucket: " + bucket + ", key:" + key, e);
        }
    }

    @Override
    public InputStream getBlobObject(String bucket, String key) {
        validateBucketNameOrThrow(bucket);
        validateKeyOrThrow(key);

        try {
            return s3client.getObject(bucket, key).getObjectContent();
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to getBlobObject: + bucket: " + bucket + ", key:" + key, e);
        }

    }

    @Override
    public InputStream getBlobObject(String bucket, String key, long offset) {
        validateBucketNameOrThrow(bucket);
        validateKeyOrThrow(key);

        try {
            return s3client.getObject(new GetObjectRequest(bucket, key).withRange(offset)).getObjectContent();
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to getBlobObject: + bucket: " + bucket + ", key:" + key + ", offsez " + offset, e);
        }
    }

    @Override
    public void putBlobObject(String bucket, String key, Path file) {
        validateBucketNameOrThrow(bucket);
        validateKeyOrThrow(key);
        if(file == null) throw new IllegalArgumentException("file must not be null!");

        try {
            s3client.putObject(bucket, key, file.toFile());
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to putBlobObject: + bucket: " + bucket + ", key:" + key, e);
        }
    }

    @Override
    public void putBlobObject(String bucket, String key, InputStream objectStream) {
        validateBucketNameOrThrow(bucket);
        validateKeyOrThrow(key);
        if(objectStream == null) throw new IllegalArgumentException("objectStream must not be null!");


        try {
            s3client.putObject(bucket, key, objectStream, new ObjectMetadata());
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to putBlobObject: + bucket: " + bucket + ", key:" + key, e);
        }
    }

    @Override
    public void deleteBlobObject(String bucket, String key) {
        validateBucketNameOrThrow(bucket);
        validateKeyOrThrow(key);


        try {
            s3client.deleteObject(bucket, key);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to deleteBlobObject: + bucket: " + bucket + ", key:" + key, e);
        }
    }

    @Override
    public void copyBlobObject(String sourceBucket, String sourceKey, String destinationBucket, String destinationKey) {

        validateBucketNameOrThrow(sourceBucket);
        validateBucketNameOrThrow(destinationBucket);
        validateKeyOrThrow(sourceKey);
        validateKeyOrThrow(destinationKey);

        try {
            s3client.copyObject(sourceBucket, sourceKey, destinationBucket, destinationKey);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to deleteBlobObject: + sourceBucket: " + sourceBucket + ", sourceKey:" + sourceKey + ", destinationBucket " + destinationBucket + ", destinationKey " + destinationKey, e);
        }
    }

    @Override
    public String getTempGETUrl(String bucket, String key) {
        validateBucketNameOrThrow(bucket);
        validateKeyOrThrow(key);

        try {
            return s3client.generatePresignedUrl(bucket, key, expirationDate(), HttpMethod.GET).toString();
        }catch (Exception e){
            throw new ObjectStoreClientException("getTempGETUrl failed! bucket: " + bucket + ", key:" + key, e);
        }
    }

    @Override
    public String getTempPUTUrl(String bucket, String key) {
        validateBucketNameOrThrow(bucket);
        validateKeyOrThrow(key);

        try {
            return s3client.generatePresignedUrl(bucket, key, expirationDate(), HttpMethod.PUT).toString();
        }catch (Exception e){
            throw new ObjectStoreClientException("getTempPUTUrl failed! bucket: " + bucket + ", key:" + key, e);
        }
    }

    @Override
    public String getPublicUrl(String bucket, String key) {
        validateBucketNameOrThrow(bucket);
        validateKeyOrThrow(key);

        try {
            return s3client.getUrl(bucket, key).toString();
        }catch (Exception e){
            throw new ObjectStoreClientException("getPublicUrl failed! bucket: " + bucket + ", key:" + key, e);
        }
    }

    private Date expirationDate(){
        LocalDateTime now = LocalDateTime.now().plus(1, ChronoUnit.DAYS);
        return Date.from(now.toInstant(ZoneOffset.UTC));
    }

    private void validateBucketNameOrThrow(String bucket){
        if(bucket == null) throw new IllegalArgumentException("bucket must not be null!");
        if(bucket.trim().isEmpty()) throw new IllegalArgumentException("bucket must not be empty!");
    }

    private void validateKeyOrThrow(String key){
        if(key == null) throw new IllegalArgumentException("key must not be null!");
        if(key.trim().isEmpty()) throw new IllegalArgumentException("key must not be empty!");

    }
}
