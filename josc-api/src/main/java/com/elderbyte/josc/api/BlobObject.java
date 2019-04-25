package com.elderbyte.josc.api;

import com.elderbyte.josc.core.BlobObjectUtils;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 * Represents a blob object
 */
public interface BlobObject {

    /**
     * Gets the bucket name where this object is stored
     */
    String getBucket();

    /**
     * Gets the object key.
     *
     * The object key is unique inside a bucket.
     * It may contain slashes '/', which are considered as virtual directory notations.
     */
    String getObjectKey();

    /**
     * @deprecated Please switch to getObjectKey()
     */
    default String getObjectName() {
        return getObjectKey();
    }

    /**
     * The blob object size in bytes
     */
    long getLength();

    /**
     * Gets the content type (mime-type) of this object.
     */
    Optional<String> getContentType();

    /**
     * Gets the objects server side calculated hash.
     * Might not be available.
     */
    Optional<String> getObjectHash();

    /**
     * @deprecated Please switch to getObjectHash()
     */
    default String hash() {
        return getObjectHash().orElse(null);
    }

    /**
     * Last modified / creation date of this object
     */
    Optional<Instant> getLastModified();

    /**
     * Other metadata data
     */
    Map<String,String> getMetaData();

    /**
     * Returns true if this object is actually a directory.
     */
    boolean isDirectory();


    /**
     * Returns the filename of this object.
     * Slashes are interpreted as virtual directory indicators.
     *
     * @return Returns the last part after the last '/', if no '/' is found returns the input string.
     */
    default String getVirtualFileName(){
        return BlobObjectUtils.extractVirtualFileName(getObjectKey());
    }

    /**
     * Extracts the extension from this object.
     * Only the file name part is considered for extension scanning.
     *
     * @return Returns the extension with the dot, such as '.png'
     */
    default String getVirtualExtension(){
        return BlobObjectUtils.extractVirtualExtensionWithDot(getObjectKey());
    }

}
