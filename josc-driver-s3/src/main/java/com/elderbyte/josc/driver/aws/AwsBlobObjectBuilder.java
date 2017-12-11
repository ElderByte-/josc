package com.elderbyte.josc.driver.aws;

import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.elderbyte.josc.api.BlobObject;
import com.elderbyte.josc.api.Bucket;
import com.elderbyte.josc.api.ContinuableListing;
import com.elderbyte.josc.core.BlobObjectSimple;
import com.elderbyte.josc.core.BucketSimple;
import com.elderbyte.josc.core.ContinuableListingImpl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


class AwsBlobObjectBuilder {


    static ContinuableListing<BlobObject> buildChunk(ListObjectsV2Result result){

        List<BlobObject> objectList = result.getObjectSummaries().stream()
                .map(s -> AwsBlobObjectBuilder.build(s))
                .collect(Collectors.toList());

        List<BlobObject> directories = result.getCommonPrefixes().stream()
                .map(p -> AwsBlobObjectBuilder.buildDirectory(p))
                .collect(Collectors.toList());

        directories.addAll(objectList);

        return new ContinuableListingImpl<>(
                directories,
                result.getContinuationToken(),
                result.getNextContinuationToken(),
                result.getMaxKeys());
    }

    static BlobObject build(String key, ObjectMetadata meta) {
        if(meta == null) throw new IllegalArgumentException("meta was NULL!");

        Date createdTime;
        try{
            createdTime = meta.getLastModified();
        }catch (Exception e){ // Minio has a bug -> NP Exception possible
            createdTime = null;
        }

        return new BlobObjectSimple(
                key,
                meta.getContentLength(),
                toZonedDate(createdTime),
                meta.getETag(),
                false
        );
    }

    static BlobObject build(S3ObjectSummary summary){

        if(summary == null) throw new IllegalArgumentException("summary was NULL!");

        Date createdTime;
        try{
            createdTime = summary.getLastModified();
        }catch (Exception e){ // Minio has a bug -> NP Exception possible
            createdTime = null;
        }

        return new BlobObjectSimple(
                summary.getKey(),
                summary.getSize(),
                toZonedDate(createdTime),
                summary.getETag(),
                false
        );
    }

    static BlobObject buildDirectory(String prefix) {
        return new BlobObjectSimple(
                prefix,
                0,
                null,
                null,
                true
        );
    }

    static Bucket build(com.amazonaws.services.s3.model.Bucket awsBucket){

        if(awsBucket == null) throw new IllegalArgumentException("awsBucket was NULL!");

        Date createdDate;
        try{
            createdDate = awsBucket.getCreationDate();
        }catch (Exception e){
            createdDate = null;
        }

        return new BucketSimple(
                awsBucket.getName(),
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
