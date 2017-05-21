package com.elderbyte.josc.driver.swift;

import com.elderbyte.josc.api.BlobObject;
import com.elderbyte.josc.api.Bucket;
import com.elderbyte.josc.core.BlobObjectSimple;
import com.elderbyte.josc.core.BucketSimple;
import org.javaswift.joss.model.Container;
import org.javaswift.joss.model.StoredObject;

import java.sql.Blob;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;


class SwiftBlobObjectBuilder {
    public static BlobObject build(StoredObject swiftObject){
        return new BlobObjectSimple(
                swiftObject.getName(),
                swiftObject.getContentLength(),
                ZonedDateTime.ofInstant(swiftObject.getLastModifiedAsDate().toInstant(), ZoneId.systemDefault()),
                swiftObject.getEtag(),
                swiftObject.isDirectory()
        );
    }

    public static Bucket build(Container container){
        return new BucketSimple(container.getName(), LocalDateTime.now()); // TODO get creation date
    }
}
