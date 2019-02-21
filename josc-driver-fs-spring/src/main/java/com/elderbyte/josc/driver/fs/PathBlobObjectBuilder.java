package com.elderbyte.josc.driver.fs;

import com.elderbyte.josc.api.BlobObject;
import com.elderbyte.josc.api.ObjectStoreClientException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class PathBlobObjectBuilder {

    /**
     * Build a blob-object for a given path
     * @param path The absolute path
     * @param bucketPath The base bucket path
     * @return A blob object abstraction
     */
    public static BlobObject from(Path path, Path bucketPath){

        if (path == null) throw new IllegalArgumentException("path must not be null");
        if (bucketPath == null) throw new IllegalArgumentException("bucketPath must not be null");

        return new PathBlobObject(
                bucketPath.getFileName().toString(),
                path,
                PathRelativizer.relativize(path, bucketPath).toString());
    }

    static class PathBlobObject implements BlobObject {
        /***************************************************************************
         *                                                                         *
         * Fields                                                                  *
         *                                                                         *
         **************************************************************************/

        private final String bucket;
        private final Path path;
        private final String key;

        /***************************************************************************
         *                                                                         *
         * Constructors                                                            *
         *                                                                         *
         **************************************************************************/

        /**
         * Creates a new PathBlobObjectBuilder
         */
        PathBlobObject(String bucket, Path path, String key) {

            if (bucket == null) throw new IllegalArgumentException("bucket");
            if (path == null) throw new IllegalArgumentException("path");
            if (key == null || key.isEmpty()) throw new IllegalArgumentException("key: " + key);

            this.bucket = bucket;
            this.path = path;
            this.key = key;
        }

        /***************************************************************************
         *                                                                         *
         * Properties                                                              *
         *                                                                         *
         **************************************************************************/

        @Override
        public String getBucket() {
            return bucket;
        }

        @Override
        public String getObjectName() {
            return key;
        }

        @Override
        public long getLength() {
            try {
                return Files.size(path);
            } catch (IOException e) {
                throw new ObjectStoreClientException("Failed to get file size.", e);
            }
        }

        @Override
        public Optional<String> getContentType() {
            return Optional.empty();
        }

        @Override
        public Optional<String> getObjectHash() {
            return Optional.empty();
        }

        @Override
        public Optional<OffsetDateTime> getLastModified() {
            try {
                return Optional.of(
                        Files.getLastModifiedTime(path)
                        .toInstant()
                        .atOffset(ZoneOffset.UTC)
                );
            } catch (IOException e) {
                return Optional.empty();
            }
        }

        @Override
        public Map<String, String> getMetaData() {
            return new HashMap<>();
        }

        @Override
        public boolean isDirectory() {
            return Files.isDirectory(path);
        }

        @Override
        public String toString() {
            return "PathBlobObject{" +
                    "path=" + path +
                    ", key='" + key + '\'' +
                    ", dir='" + isDirectory() + '\'' +
                    '}';
        }


    }
}
