package com.elderbyte.josc.driver.aws;

import com.elderbyte.josc.api.*;
import com.elderbyte.josc.api.Bucket;
import com.elderbyte.josc.core.BucketSimple;
import io.minio.MinioClient;
import io.minio.errors.*;
import org.xmlpull.v1.XmlPullParserException;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class AwsS3ObjectStoreClient implements ObjectStoreClient {

    private static final int MAX_KEYS = 1000;
    private final S3Client s3client;
    private final MinioClient minioClient;

    private static final TemporalAmount DEFAULT_EXPIRI = Duration.ofDays(1);


    public AwsS3ObjectStoreClient(S3Client s3client, MinioClient minioClient){
        if(s3client == null) throw new IllegalArgumentException("s3client must not be null!");
        this.s3client = s3client;
        this.minioClient = minioClient;
    }


    @Override
    public Stream<Bucket> listBuckets() {
        try {
            return s3client.listBuckets().buckets().stream()
                    .map(b -> AwsBlobObjectBuilder.build(b));
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to listBuckets!", e);
        }
    }

    @Override
    public boolean bucketExists(String bucket) {
        validateBucketNameOrThrow(bucket);

        try {
            s3client.headBucket(HeadBucketRequest.builder().bucket(bucket).build());
            return true;
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to bucketExists + " + bucket, e);
        }
    }

    @Override
    public void removeBucket(String bucket) {
        validateBucketNameOrThrow(bucket);

        try {
            s3client.deleteBucket(DeleteBucketRequest.builder().bucket(bucket).build());
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to removeBucket + " + bucket, e);
        }
    }

    @Override
    public Bucket createBucket(String bucket) {
        validateBucketNameOrThrow(bucket);

        try {
            s3client.createBucket(CreateBucketRequest.builder().bucket(bucket).build());
            return new BucketSimple(bucket, LocalDateTime.now());
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

            ListObjectsV2Response result = s3client.listObjectsV2(ListObjectsV2Request.builder()
                    .bucket(bucket)
                    .prefix(keyPrefix)
                    .delimiter(recursive ? null : "/")
                    .maxKeys(maxKeys)
                    .continuationToken(nextContinuationToken)
                    .build());

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
            HeadObjectResponse meta = s3client.headObject(HeadObjectRequest.builder().bucket(bucket).key(key).build());
            return AwsBlobObjectBuilder.build(key, meta);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to getBlobObjectInfo: + bucket: " + bucket + ", key:" + key, e);
        }
    }

    public boolean objectExists(String bucket, String key){
        validateBucketNameOrThrow(bucket);
        validateKeyOrThrow(key);

        try {
            getBlobObjectInfo(bucket, key);
            return true;
        }catch (Exception e){
            // throw new ObjectStoreClientException("Failed to objectExists: + bucket: " + bucket + ", key:" + key, e);
            return false;
        }
    }

    @Override
    public InputStream getBlobObject(String bucket, String key) {
        validateBucketNameOrThrow(bucket);
        validateKeyOrThrow(key);

        try {
            return s3client.getObject(
                    GetObjectRequest.builder()
                            .bucket(bucket)
                            .key(key)
                            .build()
            );
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to getBlobObject: + bucket: " + bucket + ", key:" + key, e);
        }
    }

    @Override
    public InputStream getBlobObject(String bucket, String key, long offset) {
        try {
            return minioClient.getObject(bucket, key, 0); // TODO Switch to aws sdk
        } catch (Exception e) {
            throw new ObjectStoreClientException("Failed to getBlobObject: + bucket: " + bucket + ", key:" + key, e);
        }
    }

    @Override
    public void putBlobObject(String bucket, String key, Path file) {
        validateBucketNameOrThrow(bucket);
        validateKeyOrThrow(key);
        if(file == null) throw new IllegalArgumentException("file must not be null!");

        try {
            s3client.putObject(PutObjectRequest.builder().bucket(bucket).key(key).build(), file);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to putBlobObject: + bucket: " + bucket + ", key:" + key, e);
        }
    }

    @Override
    public void putBlobObject(String bucket, String key, InputStream objectStream, long contentLength) {
        validateBucketNameOrThrow(bucket);
        validateKeyOrThrow(key);
        if(objectStream == null) throw new IllegalArgumentException("objectStream must not be null!");

        try {
            s3client.putObject(PutObjectRequest.builder().bucket(bucket).key(key).build(), RequestBody.of(objectStream, contentLength));
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to putBlobObject: + bucket: " + bucket + ", key:" + key, e);
        }
    }

    @Override
    public void deleteBlobObject(String bucket, String key) {
        validateBucketNameOrThrow(bucket);
        validateKeyOrThrow(key);


        try {
            s3client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
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
            s3client.copyObject(CopyObjectRequest.builder()
                    .copySource(sourceBucket + "/" + sourceKey) // TODO Url encode?
                    .bucket(destinationBucket)
                    .key(destinationKey)
                    .build()
            );
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to deleteBlobObject: + sourceBucket: " + sourceBucket + ", sourceKey:" + sourceKey + ", destinationBucket " + destinationBucket + ", destinationKey " + destinationKey, e);
        }
    }

    @Override
    public String getTempGETUrl(String bucket, String key) {
        return getTempGETUrl(bucket, key, DEFAULT_EXPIRI);
    }

    @Override
    public String getTempGETUrl(String bucket, String key, TemporalAmount temporalAmount) {
        validateBucketNameOrThrow(bucket);
        validateKeyOrThrow(key);

        try {
            // GetObjectRequest request = GetObjectRequest.builder().bucket(bucket).key(key).build();
            return minioClient.presignedGetObject(bucket, key, (int)temporalAmount.get(ChronoUnit.SECONDS)); // TODO SDK V2 Does not yet support presigned urls
        }catch (Exception e){
            throw new ObjectStoreClientException("getTempGETUrl failed! bucket: " + bucket + ", key:" + key, e);
        }
    }

    @Override
    public String getTempPUTUrl(String bucket, String key) {
        return getTempPUTUrl(bucket, key, DEFAULT_EXPIRI);
    }

    @Override
    public String getTempPUTUrl(String bucket, String key, TemporalAmount temporalAmount) {
        validateBucketNameOrThrow(bucket);
        validateKeyOrThrow(key);

        try {
            return minioClient.presignedPutObject(bucket, key, (int)temporalAmount.get(ChronoUnit.SECONDS)); // TODO SDK V2 Does not yet support presigned urls
        }catch (Exception e){
            throw new ObjectStoreClientException("getTempPUTUrl failed! bucket: " + bucket + ", key:" + key, e);
        }
    }

    @Override
    public String getPublicUrl(String bucket, String key) {
        validateBucketNameOrThrow(bucket);
        validateKeyOrThrow(key);

        try {
            return minioClient.getObjectUrl(bucket, key); // TODO SDK V2 Does not yet support building urls
        }catch (Exception e){
            throw new ObjectStoreClientException("getPublicUrl failed! bucket: " + bucket + ", key:" + key, e);
        }
    }



    private Date toDateFromNow(TemporalAmount temporalAmount){
        LocalDateTime now = LocalDateTime.now().plus(temporalAmount);
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
