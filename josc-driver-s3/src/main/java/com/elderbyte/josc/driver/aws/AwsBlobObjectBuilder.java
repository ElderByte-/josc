package com.elderbyte.josc.driver.aws;


import com.elderbyte.josc.api.BlobObject;
import com.elderbyte.josc.api.Bucket;
import com.elderbyte.josc.api.ContinuableListing;
import com.elderbyte.josc.core.BlobObjectSimple;
import com.elderbyte.josc.core.BucketSimple;
import com.elderbyte.josc.core.ContinuableListingImpl;
import software.amazon.awssdk.services.s3.model.CommonPrefix;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;


class AwsBlobObjectBuilder {


    static ContinuableListing<BlobObject> buildChunk(ListObjectsV2Response result){



        List<BlobObject> objectList = result.contents().stream()
                .map(s -> AwsBlobObjectBuilder.build(s))
                .collect(Collectors.toList());

        List<BlobObject> directories = result.commonPrefixes().stream()
                .map(p -> AwsBlobObjectBuilder.buildDirectory(p))
                .collect(Collectors.toList());

        directories.addAll(objectList);

        return new ContinuableListingImpl<>(
                directories,
                result.continuationToken(),
                result.nextContinuationToken(),
                result.maxKeys());
    }

    static BlobObject build(String key, HeadObjectResponse meta) {
        if(meta == null) throw new IllegalArgumentException("meta was NULL!");

        Instant createdTime;
        try{
            createdTime = meta.lastModified();
        }catch (Exception e){ // Minio has a bug -> NP Exception possible
            createdTime = null;
        }

        return new BlobObjectSimple(
                key,
                meta.contentLength(),
                toZonedDate(createdTime),
                meta.eTag(),
                false
        );
    }

    static BlobObject build(S3Object summary){

        if(summary == null) throw new IllegalArgumentException("summary was NULL!");

        Instant createdTime;
        try{
            createdTime = summary.lastModified();
        }catch (Exception e){ // Minio has a bug -> NP Exception possible
            createdTime = null;
        }

        return new BlobObjectSimple(
                summary.key(),
                summary.size(),
                toZonedDate(createdTime),
                summary.eTag(),
                false
        );
    }

    static BlobObject buildDirectory(CommonPrefix prefix) {

        return new BlobObjectSimple(
                prefix.prefix(),
                0,
                null,
                null,
                true
        );
    }

    static Bucket build(software.amazon.awssdk.services.s3.model.Bucket awsBucket){

        if(awsBucket == null) throw new IllegalArgumentException("awsBucket was NULL!");

        Instant createdDate;
        try{
            createdDate = awsBucket.creationDate();
        }catch (Exception e){
            createdDate = null;
        }

        return new BucketSimple(
                awsBucket.name(),
                toLocalDate(createdDate));
    }

    private static ZonedDateTime toZonedDate(Instant date){
        if(date != null){
            return ZonedDateTime.ofInstant(date, ZoneId.systemDefault());
        }else{
            return ZonedDateTime.now();
        }
    }

    private static LocalDateTime toLocalDate(Instant date){
        if(date != null){
            return LocalDateTime.ofInstant(date, ZoneId.systemDefault());
        }else{
            return LocalDateTime.now();
        }
    }



}
