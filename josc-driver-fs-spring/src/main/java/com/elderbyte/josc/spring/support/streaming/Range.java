package com.elderbyte.josc.spring.support.streaming;

/**
 * This class represents a byte range.
 */
class Range {
    final long start;
    final long end;
    final long length;
    final long total;

    /**
     * Creates a full range for the given total size
     * @param total
     * @return
     */
    public static Range createFull(long total){
        return new Range(0, total-1, total);
    }

    /**
     * Construct a byte range.
     * @param start Start of the byte range.
     * @param end End of the byte range.
     * @param total Total length of the byte source.
     */
    public Range(long start, long end, long total) {
        this.start = start;
        this.end = end;
        this.length = end - start + 1;
        this.total = total;
    }

    public boolean isFull(){
        return length == total;
    }

    @Override
    public String toString() {
        return "Range{" +
            "start=" + start +
            ", end=" + end +
            ", length=" + length +
            ", total=" + total +
            '}';
    }
}
