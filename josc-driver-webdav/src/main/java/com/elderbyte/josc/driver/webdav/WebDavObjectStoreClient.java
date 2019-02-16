package com.elderbyte.josc.driver.webdav;

import com.elderbyte.josc.api.*;
import com.elderbyte.josc.core.BlobObjectSimple;
import com.elderbyte.josc.core.BucketSimple;
import com.elderbyte.josc.core.ContinuableListingImpl;
import com.github.sardine.DavResource;
import com.github.sardine.Sardine;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.TemporalAmount;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class WebDavObjectStoreClient implements ObjectStoreClient {

    private final Sardine sardine;
    private final String baseUrl;
    private final String account;
    private final String key;

    private final String host;

    public WebDavObjectStoreClient(Sardine sardine, String baseUrl, String account, String key){
        this.sardine = sardine;
        this.baseUrl = baseUrl;
        this.account = account;
        this.key = key;

        this.host = getHostOnly(baseUrl);
    }


    @Override
    public Stream<Bucket> listBuckets() {
        try {
            return sardine.list(baseUrl.toString()).stream()
                .filter(r -> r.isDirectory())
                .map(r -> new BucketSimple(
                    r.getName(),
                    LocalDateTime.ofInstant(r.getCreation().toInstant(), ZoneId.systemDefault())));

        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to list buckets!", e);
        }
    }

    @Override
    public boolean bucketExists(String bucket) {
        try {
            return sardine.exists(getBucketUrl(bucket));
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to check bucket!", e);
        }
    }

    @Override
    public void removeBucket(String bucket) {
        try {
            sardine.delete(getBucketUrl(bucket));
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to delete bucket!", e);
        }
    }

    @Override
    public Bucket createBucket(String bucket) {
        try {
            sardine.createDirectory(getBucketUrl(bucket));
            return new BucketSimple(
                    bucket,
                    LocalDateTime.now()
            );
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to create bucket!", e);
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


        String rootFolder = getBucketUrl(bucket);

        String filePrefix = "";
        if(keyPrefix != null){
            String pathPrefix = keyPrefix;
            int lastSeparator = keyPrefix.lastIndexOf("/");
            if(lastSeparator > 0){
                pathPrefix = keyPrefix.substring(0, lastSeparator);
                filePrefix = keyPrefix.substring(lastSeparator+1, keyPrefix.length());
            }
            rootFolder = rootFolder + "/" + pathPrefix;
        }

        return listBlobObjectsDav(rootFolder, filePrefix, recursive, bucket);
    }

    @Override
    public ContinuableListing<BlobObject> listBlobObjectsChunked(String bucket, String keyPrefix, boolean recursive, int maxObjects, String nextContinuationToken) {

        // TODO Handle pagination

        return new ContinuableListingImpl<>(
                listBlobObjects(bucket, keyPrefix, recursive).collect(Collectors.toList()),
                null,
                null,
                maxObjects
        );
    }

    @Override
    public BlobObject getBlobObjectInfo(String bucket, String key) {
        return listBlobObjects(bucket, key, false)
            .findFirst()
            .orElseThrow(()-> new ObjectStoreClientException("Could not find object " + bucket + " / " + key));
    }

    @Override
    public InputStream getBlobObject(String bucket, String key) {
        String objectUrl = getObjectUrl(bucket, key);
        try {
            return sardine.get(objectUrl);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to download object inputstream: " + objectUrl, e);
        }
    }

    @Override
    public InputStream getBlobObject(String bucket, String key, long offset) {
        InputStream is = getBlobObject(bucket, key);
        try {
            is.skip(offset); // TODO do this server side using byte-range header!
        } catch (IOException e) {
            throw new ObjectStoreClientException("Failed to locally skip InputStream!", e);
        }
        return is;
    }

    @Override
    public void putBlobObject(String bucket, String key, Path file) {
        try {
            sardine.put(getObjectUrl(bucket, key), file.toFile(), Files.probeContentType(file) );
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to upload file: " + file, e);
        }
    }

    @Override
    public void putBlobObject(String bucket, String key, InputStream objectStream, long length) {
        putBlobObject(bucket, key, objectStream);
    }

    @Override
    public void putBlobObject(String bucket, String key, InputStream objectStream) {
        putBlobObject(bucket, key, objectStream, null);
    }

    @Override
    public void putBlobObject(String bucket, String key, InputStream objectStream, String contentType) {

        String putUrlStr = getObjectUrl(bucket, key);
        try {
            URI putUrl = new URI(putUrlStr);
            URI parent = putUrl.getPath().endsWith("/") ? putUrl.resolve("..") : putUrl.resolve(".");

            sardine.createDirectory(parent.toString());

            if(contentType == null){
                sardine.put(putUrlStr, objectStream);
            }else{
                sardine.put(putUrlStr, objectStream, contentType);
            }

        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to upload stream to " + putUrlStr, e);
        }
    }

    @Override
    public void deleteBlobObject(String bucket, String key) {
        try {
            sardine.delete(getObjectUrl(bucket, key));
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to delete object: " + bucket + " / " + key, e);
        }
    }

    @Override
    public void copyBlobObject(String sourceBucket, String sourceKey, String destinationBucket, String destinationKey) {
        try {
            sardine.copy(getObjectUrl(sourceBucket, sourceKey), getObjectUrl(destinationBucket, destinationKey));
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to copy object: " + sourceBucket + " / " + sourceKey + " to " + destinationBucket + " / " + destinationKey , e);
        }
    }


    @Override
    public String getTempGETUrl(String bucket, String key, Duration temporalAmount) {
        String url = getObjectUrl(bucket, key);
        return preAuthorize(url);
    }

    @Override
    public String getTempPUTUrl(String bucket, String key, Duration temporalAmount) {
        String url = getObjectUrl(bucket, key);
        return preAuthorize(url);
    }

    @Override
    public String getPublicUrl(String bucket, String key) {
        return getObjectUrl(bucket, key);
    }


    private String preAuthorize(String url){
        url = url.replace("http://", "http://" + account + ":" + key + "@");
        url = url.replace("https://", "https://" + account + ":" + key + "@");
        return url;
    }


    private String getObjectUrl(String bucket, String key){
        return getBucketUrl(bucket) + "/" + key;
    }

    private String getBucketUrl(String bucket){
        return baseUrl + "/" + bucket;
    }

    private String getFullUrl(URI relative){
        return host + relative;
    }

    private BlobObject toBlobObject(String bucket, DavResource res){

        return new BlobObjectSimple(
            getObjectKey(bucket, res),
            res.getContentLength(),
            ZonedDateTime.ofInstant(res.getCreation().toInstant(), ZoneId.systemDefault()),
            res.getEtag(),
            res.isDirectory());
    }

    private String getObjectKey(String bucket, DavResource res){
        String fullUrl = getFullUrl(res.getHref());
        return fullUrl.substring(getBucketUrl(bucket).length()+1);
    }


    private String getHostOnly(String url){
        try {
            URI uri =  new URI(url);
            String portPart = uri.getPort() > -1 ? (":" + uri.getPort()) : "";
            return uri.getScheme() + "://" + uri.getHost() + portPart;
        } catch (URISyntaxException e) {
            throw new ObjectStoreClientException("invalid url: " + url, e);
        }
    }


    private Stream<BlobObject> listBlobObjectsDav(String baseUrl, String requiredPrefix, boolean recursive, String bucket){
        try {

            boolean avoidPrefixFilter = requiredPrefix == null || requiredPrefix.isEmpty();

            return sardine.list(baseUrl).stream()
                .filter(r -> avoidPrefixFilter || r.getName().startsWith(requiredPrefix))
                .flatMap(r -> {
                    if(!r.isDirectory()){
                        return Stream.of(r);
                    }else{
                        if(recursive){
                            return listRecursive(r);
                        }
                        return Stream.empty();
                    }
                })
                .filter(r -> !r.isDirectory())
                .map(r -> toBlobObject(bucket, r));
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to list objects in " + baseUrl + " and prefix: " + requiredPrefix, e);
        }
    }

    private Stream<DavResource> listRecursive(DavResource folder) {

        String fullUrl = getFullUrl(folder.getHref());
        try {
            return sardine.list(fullUrl).stream()
                .filter(r -> !r.getHref().equals(folder.getHref()))
                .flatMap(r -> {
                    if(r.isDirectory()){
                        return listRecursive(r);
                    }else{
                        return Stream.of(r);
                    }
                });
        } catch (IOException e) {
            throw new ObjectStoreClientException("Failed to list objects in " + fullUrl, e);
        }
    }

}
