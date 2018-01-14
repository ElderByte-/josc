package com.elderbyte.josc.api;


import java.io.InputStream;
import java.nio.file.Path;
import java.time.Duration;
import java.util.stream.Stream;

/**
 * Represents a connection to a object-store.
 *
 * If an error occurs, an ObjectStoreClientException will be thrown.
 * @see ObjectStoreClientException
 */
public interface ObjectStoreClient {

    Duration DEFAULT_EXPIRI = Duration.ofDays(7);


    /***************************************************************************
     *                                                                         *
     * Bucket API                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * Lists all buckets which are available to the current user
     */
    Stream<Bucket> listBuckets();

    /**
     * Checks if the given bucket exists
     */
    boolean bucketExists(String bucket);

    /**
     * Deletes the given bucket
     */
    void removeBucket(String bucket);

    /**
     * Creates the given bucket
     */
    Bucket createBucket(String bucket);

    /***************************************************************************
     *                                                                         *
     * Blob Object methods                                                     *
     *                                                                         *
     **************************************************************************/

    // BlobObject listing methods

    /**
     * Returns all blob objects stored in the given bucket.
     */
    Stream<BlobObject> listBlobObjects(String bucket);

    /**
     * Returns all blob objects stored in the given bucket which key starts with the given prefix.
     */
    Stream<BlobObject> listBlobObjects(String bucket, String keyPrefix);

    /**
     * Returns all blob objects stored in the given bucket which key starts with the given prefix,
     * optionally you can specify non-recursive scan to limit to the current virtual directory.
     */
    Stream<BlobObject> listBlobObjects(String bucket, String keyPrefix, boolean recursive);

    default ContinuableListing<BlobObject> listBlobObjectsChunked(String bucket, String keyPrefix, boolean recursive, int maxObjects){
        return listBlobObjectsChunked(bucket, keyPrefix, recursive, maxObjects, null);
    }

    /**
     * Returns a listing of blob objects / folders matching the given prefix.
     * The listing will be paged using contunation tokens.
     *
     * @param bucket The bucket
     * @param keyPrefix The prefix filter (optional)
     * @param recursive Recursive if true, list all objects, or partition by virtual folders
     * @param maxObjects Max Objects to return in a page chunk
     * @param nextContinuationToken The continuation-token to to continue a previous listing.
     */
    ContinuableListing<BlobObject> listBlobObjectsChunked(String bucket, String keyPrefix, boolean recursive, int maxObjects, String nextContinuationToken);

    // Single BlobObject access methods

    /**
     * Returns the blob object info for the given object-key
     * @param bucket The bucket
     * @param key The object key
     */
    BlobObject getBlobObjectInfo(String bucket, String key);

    /**
     * Gets the blob object data stream
     */
    InputStream getBlobObject(String bucket, String key);

    /**
     * Gets the blob object data stream with the given offset.
     */
    InputStream getBlobObject(String bucket, String key, long offset);

    /**
     * Upload the given file to the given bucket / key.
     * @param bucket The bucket in which to store the object.
     * @param key The bucket relative object name
     * @param file The file to upload
     */
    void putBlobObject(String bucket, String key, Path file);

    /**
     * Upload the given data stream to the given bucket / key.
     * @param bucket The bucket in which to store the object.
     * @param key The bucket relative object name
     * @param objectStream The data stream
     */
    void putBlobObject(String bucket, String key, InputStream objectStream, long length);

    /**
     * Deletes the blob object at the given bucket / key.
     */
    void deleteBlobObject(String bucket, String key);

    /**
     * Copies the given blob object to the given destination.
     *
     * @param sourceBucket The source bucket
     * @param sourceKey The source blob key
     * @param destinationBucket The destination bucket
     * @param destinationKey The destination key
     */
    void copyBlobObject(String sourceBucket, String sourceKey, String destinationBucket, String destinationKey);

    /***************************************************************************
     *                                                                         *
     * Pre signed urls                                                         *
     *                                                                         *
     **************************************************************************/

    /**
     * Returns a direct and signed URL to this blob store which will return the given object.
     * @param bucket The bucket
     * @param key The object key
     * @param expireIn Amount of time after the url will be invalid
     * @return A signed url for GET
     */
    String getTempGETUrl(String bucket, String key, Duration expireIn);

    /**
     * Returns a direct and signed URL to this blob store which will return the given object.
     * The url will have a default expiration time of 1 day.
     * @param bucket The bucket
     * @param key The object key
     * @return A signed url
     */
    default String getTempGETUrl(String bucket, String key){
        return getTempGETUrl(bucket, key, DEFAULT_EXPIRI);
    }

    /**
     * Returns a direct and signed URL to this blob store to which a new object can be uploaded.
     * @param bucket The bucket
     * @param key The object key
     * @param expireIn Amount of time after the url will be invalid
     * @return A signed url for PUT
     */
    String getTempPUTUrl(String bucket, String key, Duration expireIn);

    /**
     * Returns a direct and signed URL to this blob store to which a new object can be uploaded.
     * The url will have a default expiration time of 1 day.
     * @param bucket The bucket
     * @param key The object key
     * @return A signed url for PUT
     */
    default String getTempPUTUrl(String bucket, String key){
        return getTempGETUrl(bucket, key, DEFAULT_EXPIRI);
    }

    /**
     * Returns a direct url to this blob without any signing.
     * The blob must therefore be available without a authentication.
     */
    String getPublicUrl(String bucket, String key);

}
