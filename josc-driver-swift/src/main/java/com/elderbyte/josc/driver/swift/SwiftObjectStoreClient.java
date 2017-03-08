package com.elderbyte.josc.driver.swift;

import com.elderbyte.josc.core.BlobObjectSimple;
import com.elderbyte.josc.core.BucketSimple;
import com.elderbyte.josc.api.*;
import com.elderbyte.josc.core.Streams;
import org.javaswift.joss.headers.object.range.ExcludeStartRange;
import org.javaswift.joss.instructions.DownloadInstructions;
import org.javaswift.joss.model.Account;
import org.javaswift.joss.model.Container;
import org.javaswift.joss.model.StoredObject;


import java.io.InputStream;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.stream.Stream;


public class SwiftObjectStoreClient implements ObjectStoreClient {


    private final Account swiftClient;

    public SwiftObjectStoreClient(Account swiftClient){
        this.swiftClient = swiftClient;
    }

    @Override
    public Stream<Bucket> listBuckets() {

        try {
            return swiftClient.list().stream()
                .map(b -> new BucketSimple(b.getName(), LocalDateTime.now())); // TODO get creation date
                    //, ZoneId.systemDefault()))); // LocalDateTime.ofInstant(b.get().toInstant()
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to list buckets!", e);
        }
    }

    @Override
    public boolean bucketExists(String bucket) {
        try {
            return swiftClient.getContainer(bucket).exists();
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to check if bucket "+bucket+" exists!", e);
        }
    }

    @Override
    public void removeBucket(String bucket) {
        try {
            swiftClient.getContainer(bucket).delete();
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to remove bucket "+bucket+"!", e);
        }
    }

    @Override
    public void createBucket(String bucket) {
        try {
            swiftClient.getContainer(bucket).create();
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to create bucket "+bucket+"!", e);
        }
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

        try {
            return swiftClient.getContainer(bucket).list(keyPrefix, null, 99999) // TODO Handle pagination
                    .stream()
                    .map(obj -> new SwiftBlobObject(obj));

        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to list bucket: "+bucket+" and  prefix: "+keyPrefix+ "!", e);
        }
    }

    @Override
    public InputStream getBlobObject(String bucket, String key) {
        try {
            return swiftClient.getContainer(bucket).getObject(key).downloadObjectAsInputStream();
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to get InputStream of object: " + bucket +" / " + key, e);
        }
    }

    @Override
    public InputStream getBlobObject(String bucket, String key, long offset) {
        try {
            return swiftClient.getContainer(bucket).getObject(key)
                    .downloadObjectAsInputStream(
                            new DownloadInstructions()
                            .setRange(new ExcludeStartRange((int)offset)));
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to get InputStream of object: " + bucket +" / " + key, e);
        }
    }

    @Override
    public BlobObject getBlobObjectInfo(String bucket, String key) {
        try {

            StoredObject object = swiftClient.getContainer(bucket).getObject(key);

            return new BlobObjectSimple(
                key,
                object.getContentLength(),
                ZonedDateTime.ofInstant(object.getLastModifiedAsDate().toInstant(),
                ZoneId.systemDefault()),
                    object.getEtag()
            );

        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to get Object-Info of object: " + bucket +" / " + key, e);
        }
    }

    @Override
    public void putBlobObject(String bucket, String key, Path file){
        try {
            swiftClient.getContainer(bucket).getObject(key)
                    .uploadObject(file.toFile());
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to upload blob object: "  + bucket +" / " + key + " from file " + file, e);
        }
    }

    @Override
    public void putBlobObject(String bucket, String key, InputStream objectStream, long length, String mimeType){
        try {
            swiftClient.getContainer(bucket).getObject(key)
                    .uploadObject(objectStream);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to upload blob object.", e);
        }
    }

    @Override
    public void deleteBlobObject(String bucket, String key) {
        try {
            swiftClient.getContainer(bucket).getObject(key).delete();
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to delete object " + bucket + " : " + key, e);
        }
    }

    @Override
    public String getTempGETUrl(String bucket, String key) {
        try {
            return swiftClient.getContainer(bucket).getObject(key).getTempGetUrl(3600 * 24);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to create presigned GetObject url " + bucket + " : " + key, e);
        }
    }

    @Override
    public String getTempPUTUrl(String bucket, String key) {
        try {
            return swiftClient.getContainer(bucket).getObject(key).getTempPutUrl(3600 * 24);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to create presigned PutObject " + bucket + " : " + key, e);
        }
    }

    public void copyBlobObject(String sourceBucket, String sourceKey, String destinationBucket, String destinationKey){
        try {
            Container destination = swiftClient.getContainer(destinationBucket);

            swiftClient.getContainer(sourceBucket).getObject(sourceKey)
                    .copyObject(destination, destination.getObject(destinationKey));

        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to copy " + sourceBucket + " / " + sourceKey + " to " + destinationBucket + " / " + destinationKey, e);
        }
    }

    public String getPublicUrl(String bucket, String key){
        try {
            return swiftClient.getContainer(bucket).getObject(key).getPublicURL();
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to get public object url " + bucket + " : " + key, e);
        }
    }


}
