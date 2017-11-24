package com.elderbyte.josc.core;

import com.elderbyte.josc.api.ContinuableListing;

import java.util.List;

public class ContinuableListingImpl<T> implements ContinuableListing<T> {

    private List<T> data;
    private String nextContinuationToken;
    private String continuationToken;
    private int maxPageSize;

    public ContinuableListingImpl(List<T> data, String currentToken, String nextToken, int pageSize){
        this.data = data;
        this.continuationToken = currentToken;
        this.nextContinuationToken = nextToken;
        this.maxPageSize = pageSize;
    }

    @Override
    public List<T> getContent() {
        return data;
    }

    @Override
    public String getContinuationToken() {
        return continuationToken;
    }

    @Override
    public String getNextContinuationToken() {
        return nextContinuationToken;
    }

    @Override
    public int getSize() {
        return maxPageSize;
    }

    @Override
    public boolean hasMore() {
        return nextContinuationToken != null;
    }
}
