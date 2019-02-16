package com.elderbyte.josc.core;

import com.elderbyte.josc.api.BlobObject;

import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by isnull on 06/09/16.
 */
public class BlobObjectSimple implements BlobObject {


    /***************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/

    private final String objectName;
    private final long length;
    private final String hash;
    private final OffsetDateTime lastModified;
    private final boolean isDirectory;
    private static final Map<String, String> metaData = new HashMap<>(); // Not used for now

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public BlobObjectSimple(String objectName, long length, OffsetDateTime lastModified, String hash, boolean isDirectory){
        this.objectName = objectName;
        this.length = length;
        this.lastModified = lastModified;
        this.hash = hash;
        this.isDirectory = isDirectory;
    }

    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    @Override
    public String getObjectName() {
        return objectName;
    }

    @Override
    public long getLength() {
        return length;
    }

    @Override
    public String hash() {
        return hash;
    }

    @Override
    public OffsetDateTime getLastModified() {
        return lastModified;
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
