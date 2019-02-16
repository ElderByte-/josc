package com.elderbyte.josc.core;

import com.elderbyte.josc.api.BlobObject;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by isnull on 06/09/16.
 */
public class BlobObjectSimple implements BlobObject {


    /***************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/

    private final String bucket;
    private final String objectName;
    private final long length;
    private final String hash;
    private final OffsetDateTime lastModified;
    private final boolean isDirectory;
    private final String contentType;
    private static final Map<String, String> metaData = Collections.emptyMap(); // Not used for now

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public BlobObjectSimple(
            String bucket,
            String objectName,
            long length,
            OffsetDateTime lastModified,
            String hash,
            boolean isDirectory,
            String contentType
    ){

        if (bucket == null) throw new IllegalArgumentException("bucket");
        if (objectName == null) throw new IllegalArgumentException("objectName");
        if (lastModified == null) throw new IllegalArgumentException("lastModified");

        this.bucket = bucket;
        this.objectName = objectName;
        this.length = length;
        this.lastModified = lastModified;
        this.hash = hash;
        this.isDirectory = isDirectory;
        this.contentType = contentType;
    }

    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    @Override
    public String getBucket() {
        return bucket;
    }

    @Override
    public String getObjectName() {
        return objectName;
    }

    @Override
    public long getLength() {
        return length;
    }

    @Override
    public Optional<String> getObjectHash() {
        return Optional.ofNullable(hash);
    }

    @Override
    public OffsetDateTime getLastModified() {
        return lastModified;
    }

    @Override
    public Optional<String> getContentType() {
        return Optional.ofNullable(contentType);
    }


    @Override
    public Map<String, String> getMetaData() {
        return metaData;
    }

    @Override
    public boolean isDirectory() {
        return isDirectory;
    }


}
