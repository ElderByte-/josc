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

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


class AwsBlobObjectBuilder {


    static ContinuableListing<BlobObject> buildChunk(String bucket, ListObjectsV2Response result){

        if(result == null) throw new IllegalArgumentException("result was NULL!");

        List<BlobObject> objectList = result.contents() != null ? result.contents().stream()
                .map(o -> AwsBlobObjectBuilder.build(bucket, o))
                .collect(Collectors.toList()) : new ArrayList<>();

        List<BlobObject> directories = result.commonPrefixes() != null ? result.commonPrefixes().stream()
                .map(o -> AwsBlobObjectBuilder.buildDirectory(bucket, o))
                .collect(Collectors.toList()) : new ArrayList<>();

        directories.addAll(objectList);

        return new ContinuableListingImpl<>(
                directories,
                result.continuationToken(),
                result.nextContinuationToken(),
                Optional.ofNullable(result.maxKeys()).orElse(0));
    }

    static BlobObject build(String bucket, String key, HeadObjectResponse meta) {
        if(meta == null) throw new IllegalArgumentException("meta was NULL!");

        Instant createdTime;
        try{
            createdTime = meta.lastModified();
        }catch (Exception e){ // Minio has a bug -> NP Exception possible
            createdTime = null;
        }

        return new BlobObjectSimple(
                bucket,
                key,
                Optional.ofNullable(meta.contentLength()).orElse(0L),
                createdTime,
                trimQuotes(meta.eTag()), // AWS S3 Quirk as it quotes Etag
                false,
                meta.contentType()
        );
    }


    static BlobObject build(String bucket, S3Object summary){

        if(summary == null) throw new IllegalArgumentException("summary was NULL!");

        Instant createdTime;
        try{
            createdTime = summary.lastModified();
        }catch (Exception e){ // Minio has a bug -> NP Exception possible
            createdTime = null;
        }

        return new BlobObjectSimple(
                bucket,
                summary.key(),
                Optional.ofNullable(summary.size()).orElse(0L),
                createdTime,
                trimQuotes(summary.eTag()), // AWS S3 Quirk as it quotes Etag
                false,
                null

        );
    }

    static BlobObject buildDirectory(String bucket, CommonPrefix prefix) {

        return new BlobObjectSimple(
                bucket,
                prefix.prefix(),
                0,
                null,
                null,
                true,
                null
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
                createdDate
        );
    }

    private static String trimQuotes(String etag){
        // https://github.com/aws/aws-sdk-net/issues/815
        // AWS S3 returns the ETag wrapped in quotes (and so does Minio) so we unwrap it here
        if(etag != null){
            if(etag.startsWith("\"")){
                etag = etag.substring(1);
            }
            if(etag.endsWith("\"")){
                etag = etag.substring(0, etag.length()-1);
            }
        }
        return etag;
    }
}
