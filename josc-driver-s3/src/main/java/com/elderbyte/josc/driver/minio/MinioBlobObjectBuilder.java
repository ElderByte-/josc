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

        Date lastModified;
        try{
            lastModified = minioItem.lastModified();
        }catch (Exception e){ // Minio has a bug -> NP Exception possible
            lastModified = null;
        }

        return new BlobObjectSimple(
                minioItem.objectName(),
                minioItem.objectSize(),
                toZonedDate(lastModified),
                minioItem.etag(),
                minioItem.isDir()
        );
    }

    static BlobObject build(ObjectStat stat){

        if(stat == null) throw new IllegalArgumentException("stat was NULL!");

        Date createdTime;
        try{
            createdTime = stat.createdTime();
        }catch (Exception e){ // Minio has a bug -> NP Exception possible
            createdTime = null;
        }

        return new BlobObjectSimple(
                stat.name(),
                stat.length(),
                toZonedDate(createdTime),
                stat.etag(),
                false
        );
    }

    static Bucket build(io.minio.messages.Bucket minioBucket){

        if(minioBucket == null) throw new IllegalArgumentException("minioBucket was NULL!");

        Date createdDate;
        try{
            createdDate = minioBucket.creationDate();
        }catch (Exception e){ // Minio has a bug -> NP Exception possible
            createdDate = null;
        }

        return new BucketSimple(
                minioBucket.name(),
                toLocalDate(createdDate));
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
