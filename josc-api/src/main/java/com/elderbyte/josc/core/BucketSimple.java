package com.elderbyte.josc.core;

import com.elderbyte.josc.api.Bucket;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;


public class BucketSimple implements Bucket {

    private final String name;
    private final OffsetDateTime creationDate;

    public BucketSimple(String name, OffsetDateTime creationDate){
        this.name = name;
        this.creationDate = creationDate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public OffsetDateTime getCreationDate() {
        return creationDate;
    }
}
