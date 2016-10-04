package com.elderbyte.josc.api;

import java.time.LocalDateTime;

/**
 * Represents a bucket in a object store.
 *
 * Buckets are the only physical structure to separate blob objects from each other.
 *
 */
public interface Bucket {

    /**
     * Get the name of this bucket
     */
    String getName();

    /**
     * Gets the creation date of this bucket
     */
    LocalDateTime getCreationDate();

}
