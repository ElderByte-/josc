package com.elderbyte.josc.spring.support.streaming;

import java.io.Closeable;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SeekableByteChannel;
import java.nio.channels.WritableByteChannel;

import static sun.nio.ch.IOStatus.EOF;

public class ChannelUtils {

    private static final int DEFAULT_BUFFER_SIZE = 10240; // ..bytes = 10KB.


    /**
     * Copy the given byte range of the given input to the given output.
     *
     * @param input  The input to copy the given range to the given output for.
     * @param output The output to copy the given range from the given input for.
     * @param start  Start of the byte range.
     * @param length Length of the byte range.
     * @throws IOException If something fails at I/O level.
     */
    public static void copy(ReadableByteChannel input, WritableByteChannel output, long start, long length)
            throws IOException {
        if (input instanceof FileChannel) {
            // If possible we use OS features for a zero-copy transfer
            ((FileChannel) input).transferTo(start, length, output);
        } else if (input instanceof SeekableByteChannel) {
            ((SeekableByteChannel) input).position(start);
            fastChannelCopy(input, output, length);
        } else {
            skip(input, start);
            fastChannelCopy(input, output, length);
        }
    }

    /**
     * Skips bytes from a ReadableByteChannel.
     * This implementation guarantees that it will read as many bytes
     * as possible before giving up.
     *
     * @param input  ReadableByteChannel to skip
     * @param toSkip number of bytes to skip.
     * @return number of bytes actually skipped.
     * @throws IOException              if there is a problem reading the ReadableByteChannel
     * @throws IllegalArgumentException if toSkip is negative
     * @since 2.5
     */
    public static long skip(final ReadableByteChannel input, final long toSkip) throws IOException {

        final int SKIP_BUFFER_SIZE = 2048;

        if (toSkip < 0) {
            throw new IllegalArgumentException("Skip count must be non-negative, actual: " + toSkip);
        }
        final ByteBuffer skipByteBuffer = ByteBuffer.allocate((int) Math.min(toSkip, SKIP_BUFFER_SIZE));
        long remain = toSkip;
        while (remain > 0) {
            skipByteBuffer.position(0);
            skipByteBuffer.limit((int) Math.min(remain, SKIP_BUFFER_SIZE));
            final int n = input.read(skipByteBuffer);
            if (n == EOF) {
                break;
            }
            remain -= n;
        }
        return toSkip - remain;
    }

    /**
     * Close the given resource relaxed.
     *
     * @param resource The resource to be closed.
     */
    public static void closeRelaxed(Closeable resource) {
        if (resource != null) {
            try {
                resource.close();
            } catch (IOException ignore) {
                // Ignored by definition
            }
        }
    }


    private static void fastChannelCopy(final ReadableByteChannel input, final WritableByteChannel output, long toRead) throws IOException {
        final ByteBuffer buffer = ByteBuffer.allocateDirect(DEFAULT_BUFFER_SIZE);
        int read;
        while (toRead > 0) {
            read = input.read(buffer);
            if (read > 0) {
                // prepare the buffer to be drained
                buffer.flip();

                if (toRead < read) {
                    buffer.limit((int) toRead);
                }

                // write to the channel, may block
                output.write(buffer);

                // If partial transfer, shift remainder down
                // If buffer is empty, same as doing clear()
                buffer.compact();
                toRead -= read;
            } else {
                break;
            }
        }
    }
}