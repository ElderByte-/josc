package com.elderbyte.josc.driver.fs;

import com.elderbyte.josc.api.BlobObject;
import com.elderbyte.josc.api.ObjectStoreClientException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;


public class PathBlobObject implements BlobObject {

    /***************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/

    private final Path path;
    private final String key;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    /**
     * Creates a new PathBlobObject
     */
    public PathBlobObject(Path path, String key){

        if(path == null) throw new IllegalArgumentException("path");
        if(key == null) throw new IllegalArgumentException("key");

        this.path = path;
        this.key = key;
    }

    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/


    @Override
    public String getObjectName() {
        return key;
    }

    @Override
    public long getLength() {
        try{
            return Files.size(path);
        }catch (IOException e){
            throw new ObjectStoreClientException("Failed to get size.", e);
        }
    }

    @Override
    public String hash() {
        return null;
    }

    @Override
    public ZonedDateTime getLastModified() {
        try {
            return ZonedDateTime.ofInstant(Files.getLastModifiedTime(path).toInstant(), ZoneId.systemDefault());
        }catch (IOException e){
            throw new ObjectStoreClientException("Failed to get size.", e);
        }
    }

    @Override
    public Map<String, String> getMetaData() {
        return new HashMap<>();
    }


    @Override
    public String toString() {
        return "PathBlobObject{" +
            "path=" + path +
            ", key='" + key + '\'' +
            '}';
    }
}
