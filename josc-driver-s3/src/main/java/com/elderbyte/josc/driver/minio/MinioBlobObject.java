package com.elderbyte.josc.driver.minio;

import com.elderbyte.josc.api.BlobObject;
import io.minio.messages.Item;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;


public class MinioBlobObject implements BlobObject {

    /***************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/

    private final Item minioItem;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public MinioBlobObject(Item minioItem){
        this.minioItem = minioItem;
    }

    @Override
    public String getObjectName() {
        return minioItem.objectName();
    }

    @Override
    public long getLength(){
        return minioItem.objectSize();
    }


    public String hash(){
        return minioItem.etag();
    }

    @Override
    public ZonedDateTime getLastModified() {
        return ZonedDateTime.ofInstant(minioItem.lastModified().toInstant(), ZoneId.systemDefault());
    }

    @Override
    public Map<String, String> getMetaData() {
        return new HashMap<>();// TODO minioItem.getUnknownKeys().;
    }

    @Override
    public String toString() {
        return "MinioBlobObject{" +
            "minioItem=" + minioItem +
            '}';
    }
}
