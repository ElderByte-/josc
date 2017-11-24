package com.elderbyte.josc.api;


import java.io.InputStream;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Represents a connection to a object-store.
 *
 * If an error occurs, an ObjectStoreClientException will be thrown.
 * @see ObjectStoreClientException
 */
public interface ObjectStoreClient {


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
    void createBucket(String bucket);

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
     * @param length The total length of the stream
     * @param mimeType The content type in mime type format, example: 'video/mp4'
     */
    void putBlobObject(String bucket, String key, InputStream objectStream, long length, String mimeType);

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
     */
    String getTempGETUrl(String bucket, String key);

    /**
     * Returns a direct and signed URL to this blob store to which a new object can be uploaded.
     */
    String getTempPUTUrl(String bucket, String key);

    /**
     * Returns a direct url to this blob without any signing.
     * The blob must therefore be available without a authentication.
     */
    String getPublicUrl(String bucket, String key);

}
