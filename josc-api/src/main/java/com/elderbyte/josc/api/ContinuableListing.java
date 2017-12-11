package com.elderbyte.josc.api;

import java.util.List;

public interface ContinuableListing<T> {

    /**
     * The content in this listing chunk
     */
    List<T> getContent();

    /**
     * The current continuation token of this listing.
     * To fetch the next page, use the NextContinuationToken property not this!
     */
    String getContinuationToken();

    /**
     * The continuation token to fetch the next part
     */
    String getNextContinuationToken();

    /**
     * Size of this chunk (max page size)
     */
    int getSize();

    /**
     * Is there more data to load with the NextContiunationToken?
     */
    boolean hasMore();
}
