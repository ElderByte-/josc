package com.elderbyte.josc.driver.minio;

import com.elderbyte.josc.api.BlobObject;
import com.elderbyte.josc.api.Bucket;
import com.elderbyte.josc.core.BlobObjectSimple;
import com.elderbyte.josc.core.BucketSimple;
import io.minio.ObjectStat;
import io.minio.messages.Item;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;


class MinioBlobObjectBuilder {

    static BlobObject build(Item minioItem){
        return new BlobObjectSimple(
                minioItem.objectName(),
                minioItem.objectSize(),
                ZonedDateTime.ofInstant(minioItem.lastModified().toInstant(), ZoneId.systemDefault()),
                minioItem.etag(),
                minioItem.isDir()
        );
    }

    static BlobObject build(ObjectStat stat){
        return new BlobObjectSimple(
                stat.name(),
                stat.length(),
                ZonedDateTime.ofInstant(stat.createdTime().toInstant(), ZoneId.systemDefault()),
                stat.etag(),
                false
        );
    }

    static Bucket build(io.minio.messages.Bucket minioBucket){
        return new BucketSimple(
                minioBucket.name(),
                LocalDateTime.ofInstant(minioBucket.creationDate().toInstant(), ZoneId.systemDefault()));
    }
}
