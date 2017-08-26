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
import java.util.Date;


class MinioBlobObjectBuilder {

    static BlobObject build(Item minioItem){

        if(minioItem == null) throw new IllegalArgumentException("minioItem was NULL!");

        return new BlobObjectSimple(
                minioItem.objectName(),
                minioItem.objectSize(),
                toZonedDate(minioItem.lastModified()),
                minioItem.etag(),
                minioItem.isDir()
        );
    }

    static BlobObject build(ObjectStat stat){

        if(stat == null) throw new IllegalArgumentException("stat was NULL!");


        return new BlobObjectSimple(
                stat.name(),
                stat.length(),
                toZonedDate(stat.createdTime()),
                stat.etag(),
                false
        );
    }

    static Bucket build(io.minio.messages.Bucket minioBucket){

        if(minioBucket == null) throw new IllegalArgumentException("minioBucket was NULL!");

        return new BucketSimple(
                minioBucket.name(),
                toLocalDate(minioBucket.creationDate()));
    }

    private static ZonedDateTime toZonedDate(Date date){
        if(date != null){
            return ZonedDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        }else{
            return ZonedDateTime.now();
        }
    }

    private static LocalDateTime toLocalDate(Date date){
        if(date != null){
            return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
        }else{
            return LocalDateTime.now();
        }
    }
}
