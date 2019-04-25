package com.elderbyte.josc.driver.swift;

import com.elderbyte.josc.api.BlobObject;
import com.elderbyte.josc.api.Bucket;
import com.elderbyte.josc.core.BlobObjectSimple;
import com.elderbyte.josc.core.BucketSimple;
import org.javaswift.joss.model.Container;
import org.javaswift.joss.model.StoredObject;

import java.time.*;


class SwiftBlobObjectBuilder {
    public static BlobObject build(String bucket, StoredObject swiftObject){
        return new BlobObjectSimple(
                bucket,
                swiftObject.getName(),
                swiftObject.getContentLength(),
                swiftObject.getLastModifiedAsDate().toInstant(),
                swiftObject.getEtag(),
                swiftObject.isDirectory(),
                swiftObject.getContentType()
        );
    }

    public static Bucket build(Container container){
        return new BucketSimple(container.getName(), Instant.now()); // TODO get creation date
    }
}
