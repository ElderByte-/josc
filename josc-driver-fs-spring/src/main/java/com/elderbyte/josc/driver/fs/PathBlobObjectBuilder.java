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

        return new PathBlobObject(path, PathRelativizer.relativize(path, bucketPath).toString());
    }

    static class PathBlobObject implements BlobObject {
        /***************************************************************************
         *                                                                         *
         * Fields                                                                  *
         *                                                                         *
         **************************************************************************/

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
        PathBlobObject(Path path, String key) {

            if (path == null) throw new IllegalArgumentException("path");
            if (key == null || key.isEmpty()) throw new IllegalArgumentException("key: " + key);

            this.path = path;
            this.key = key;
        }

        /***************************************************************************
         *                                                                         *
         * Properties                                                              *
         *                                                                         *
         **************************************************************************/


        @Override
        public String getObjectName() {
            return key;
        }

        @Override
        public long getLength() {
            try {
                return Files.size(path);
            } catch (IOException e) {
                throw new ObjectStoreClientException("Failed to get size.", e);
            }
        }

        @Override
        public String hash() {
            return null;
        }

        @Override
        public OffsetDateTime getLastModified() {
            try {
                return Files.getLastModifiedTime(path)
                        .toInstant()
                        .atOffset(ZoneOffset.UTC);
            } catch (IOException e) {
                throw new ObjectStoreClientException("Failed to get size.", e);
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
