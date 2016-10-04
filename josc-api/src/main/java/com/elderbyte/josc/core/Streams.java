package com.elderbyte.josc.core;

import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Stream utilities
 */
public class Streams {

    public static <T> Stream<T> stream(Iterable<T> iterable) {
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(
                iterable.iterator(),
                Spliterator.ORDERED
            ),
            false
        );
    }

    public static <T> Stream<T> stream(Iterator<T> iterator) {
        return StreamSupport.stream(
            Spliterators.spliteratorUnknownSize(
                iterator,
                Spliterator.ORDERED
            ),
            false
        );
    }

}