package com.elderbyte.josc.api;

import com.elderbyte.josc.core.BlobObjectUtils;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Map;

/**
 * Represents a blob object
 */
public interface BlobObject {

    /**
     * Gets the object name (its unique key).
     *
     * The object name is a bucket unique key which allows to identify an object.
     * It may contain slashes '/', which are considered as virtual directory notations.
     */
    String getObjectName();

    /**
     * The blob object size in bytes
     */
    long getLength();

    /**
     * A unique file hash.
     */
    String hash();

    /**
     * Last modified / creation date of this object
     */
    OffsetDateTime getLastModified();

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
        return BlobObjectUtils.extractVirtualFileName(getObjectName());
    }

    /**
     * Extracts the extension from this object.
     * Only the file name part is considered for extension scanning.
     *
     * @return Returns the extension with the dot, such as '.png'
     */
    default String getVirtualExtension(){
        return BlobObjectUtils.extractVirtualExtensionWithDot(getObjectName());
    }

}
