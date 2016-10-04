package com.elderbyte.josc.driver.swift;

import com.elderbyte.josc.api.BlobObject;
import org.javaswift.joss.model.StoredObject;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;


public class SwiftBlobObject implements BlobObject {

    /***************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/

    private final StoredObject object;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public SwiftBlobObject(StoredObject object){
        this.object = object;
    }

    @Override
    public String getObjectName() {
        return object.getName();
    }

    @Override
    public long getLength(){
        return object.getContentLength();
    }

    public String hash(){
        return object.getEtag();
    }

    @Override
    public ZonedDateTime getLastModified() {
        return ZonedDateTime.ofInstant(object.getLastModifiedAsDate().toInstant(), ZoneId.systemDefault());
    }

    @Override
    public Map<String, String> getMetaData() {
        return new HashMap<>();// TODO
    }


    @Override
    public String toString() {
        return "SwiftBlobObject{" +
                "object=" + object +
                '}';
    }
}
