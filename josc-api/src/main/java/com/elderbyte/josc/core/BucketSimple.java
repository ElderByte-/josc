package com.elderbyte.josc.core;

import com.elderbyte.josc.api.Bucket;

import java.time.Instant;


public class BucketSimple implements Bucket {

    private final String name;
    private final Instant creationDate;

    public BucketSimple(String name, Instant creationDate){
        this.name = name;
        this.creationDate = creationDate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Instant getCreationDate() {
        return creationDate;
    }
}
