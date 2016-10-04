package com.elderbyte.josc.core;

import com.elderbyte.josc.api.Bucket;

import java.time.LocalDateTime;


public class BucketSimple implements Bucket {

    private final String name;
    private final LocalDateTime creationDate;

    public BucketSimple(String name, LocalDateTime creationDate){
        this.name = name;
        this.creationDate = creationDate;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public LocalDateTime getCreationDate() {
        return creationDate;
    }
}
