package com.elderbyte.josc.driver.fs;

import com.elderbyte.josc.core.BucketSimple;
import com.elderbyte.josc.api.*;
import com.elderbyte.josc.core.ContinuableListingImpl;
import com.elderbyte.josc.core.Streams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;


public class FileSystemObjectStoreClient implements ObjectStoreClient {

    /***************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/


    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final Path baseFolder;
    private final String hostUrl;

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    public FileSystemObjectStoreClient(Path baseFolder, String hostUrl){
        if(baseFolder == null) throw new IllegalArgumentException("baseFolder");
        if(hostUrl == null) throw new IllegalArgumentException("hostUrl");

        this.baseFolder = baseFolder;
        this.hostUrl = hostUrl;
    }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    @Override
    public Stream<Bucket> listBuckets() {
        try {
            return Streams.stream(Files.newDirectoryStream(baseFolder).iterator())
                    .filter(p -> Files.isDirectory(p))
                    .filter(p -> {
                        try {
                            return !Files.isHidden(p);
                        } catch (IOException e) {
                            return false;
                        }
                    })
                    .map(f -> new BucketSimple(f.getFileName().toString(), null));
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to list buckets!", e);
        }
    }

    @Override
    public boolean bucketExists(String bucket) {
        Path bucketPath = baseFolder.resolve(bucket);
        return Files.exists(bucketPath);
    }

    @Override
    public void removeBucket(String bucket) {
        try {
            Path bucketPath = baseFolder.resolve(bucket);
            Files.delete(bucketPath);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to delete bucket " + bucket, e);
        }
    }

    @Override
    public Bucket createBucket(String bucket) {
        try {
            Path bucketPath = baseFolder.resolve(bucket);
            Files.createDirectories(bucketPath);
            return new BucketSimple(bucketPath.getFileName().toString(), null);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to create bucket " + bucket, e);
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

        Path bucketPath = getBucketPath(bucket);

        Path baseDirectory = bucketPath;
        String filePrefix = "";

        logger.debug("Listing objects in bucket path " + bucketPath + " and prefix " + keyPrefix + " recursive: " + recursive);

        if(keyPrefix != null){
            String pathPrefix = keyPrefix;

            int lastSeparator = keyPrefix.lastIndexOf("/");
            if(lastSeparator > 0){
                pathPrefix = keyPrefix.substring(0, lastSeparator);
                filePrefix = keyPrefix.substring(lastSeparator+1, keyPrefix.length());
            }

            baseDirectory = baseDirectory.resolve(pathPrefix);
        }

        final String prefix = filePrefix;
        final Path base = baseDirectory;
        try {
            return Files.walk(base, recursive ? Integer.MAX_VALUE : 1)
                    .filter(p -> !base.equals(p))
                    .filter(p -> (!recursive || Files.isRegularFile(p)) &&
                            (prefix.isEmpty() || p.getFileName().startsWith(prefix)))
                    .map(p -> PathBlobObjectBuilder.from(p, bucketPath));
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to list objects " + bucket, e);
        }
    }


    @Override
    public ContinuableListing<BlobObject> listBlobObjectsChunked(String bucket, String keyPrefix, boolean recursive, int maxObjects, String nextContinuationToken) {

        // TODO Support pagination using continuation-tokens

        List<BlobObject> alldata = listBlobObjects(bucket, keyPrefix, recursive)
                .collect(Collectors.toList());
        return new ContinuableListingImpl<>(alldata, null, null, alldata.size());
    }


    @Override
    public BlobObject getBlobObjectInfo(String bucket, String key) {
        try {
            Path objectPath = getObjectPath(bucket, key);
            if(Files.exists(objectPath)){
                return PathBlobObjectBuilder.from(objectPath, getBucketPath(bucket));
            }else{
                throw new FileNotFoundException(objectPath + "(file not found)");
            }
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to get object info " + bucket + " / " + key, e);
        }
    }

    @Override
    public void putBlobObject(String bucket, String key, Path file) {
        try{
            if(Files.exists(file)){
                Path targetPath = getObjectPath(bucket, key);

                Path targetFolder = targetPath.getParent();
                if(!Files.exists(targetFolder)){
                    Files.createDirectories(targetFolder);
                }
                Files.copy(file, targetPath, REPLACE_EXISTING);
            }else{
                throw new FileNotFoundException("File not found: " + file);
            }
        }catch (IOException e){
            throw new ObjectStoreClientException("Failed to upload object " + bucket + " / " + key + "from file: " + file, e);
        }
    }

    @Override
    public void putBlobObject(String bucket, String key, InputStream objectStream) {

        try {
            Path targetPath = getObjectPath(bucket, key);

            Path targetFolder = targetPath.getParent();
            if(!Files.exists(targetFolder)){
                Files.createDirectories(targetFolder);
            }

            Files.copy(objectStream, targetPath, REPLACE_EXISTING);
        }catch (IOException e){
            throw new ObjectStoreClientException("Failed to upload object " + bucket + " / " + key + "from stream: ", e);
        }
    }

    @Override
    public InputStream getBlobObject(String bucket, String key) {
        return getBlobObject(bucket, key, 0);
    }

    @Override
    public InputStream getBlobObject(String bucket, String key, long offset) {
        try {
            Path targetPath = getObjectPath(bucket, key);

            if (Files.isReadable(targetPath)) {

                FileInputStream fis = new FileInputStream(targetPath.toFile());
                if(offset > 0) { fis.skip(offset); }
                return new BufferedInputStream(fis);
            }else{
                throw new FileNotFoundException("File not found: " + targetPath);
            }
        }catch (IOException e){
            throw new ObjectStoreClientException("Failed to read object " + bucket + " / " + key , e);
        }
    }



    @Override
    public void deleteBlobObject(String bucket, String key) {
        try {
            Path objectPath = getObjectPath(bucket, key);
            Files.delete(objectPath);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to delete object " + bucket + " : " + key, e);
        }
    }

    @Override
    public void copyBlobObject(String sourceBucket, String sourceKey, String destinationBucket, String destinationKey) {
        try {
            Path source = getObjectPath(sourceBucket, sourceKey);
            Path destination = getObjectPath(destinationBucket, destinationKey);
            Files.copy(source, destination, REPLACE_EXISTING);
        }catch (Exception e){
            throw new ObjectStoreClientException("Failed to copy object " + sourceBucket + " : " + sourceKey, e);
        }
    }

    @Override
    public String getTempGETUrl(String bucket, String key) {
        return getSignedResourceUrl(bucket, key);
    }

    @Override
    public String getTempPUTUrl(String bucket, String key) {
        return getSignedResourceUrl(bucket, key);
    }

    @Override
    public String getPublicUrl(String bucket, String key) {
        return getPublicResourceUrl(bucket, key);
    }

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

    private String getSignedResourceUrl(String bucket, String key){
        return getPublicResourceUrl(bucket, key); // TODO Sign the url
    }

    private String getPublicResourceUrl(String bucket, String key){
        PathFileReference reference =  PathFileReference.from(baseFolder, bucket, key);
        return hostUrl + reference.toRelativeTempUrl();
    }

    private Path getBucketPath(String bucket){
        return baseFolder.resolve(bucket);
    }

    private Path getObjectPath(String bucket, String key){
        return getBucketPath(bucket).resolve(key);
    }

}