package com.elderbyte.josc.driver.fs;


import java.io.UnsupportedEncodingException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

public class PathFileReference {

    /***************************************************************************
     *                                                                         *
     * Static builders                                                         *
     *                                                                         *
     **************************************************************************/

    public static PathFileReference parse(String accountEncoded, String bucket, String objectNameEncoded){

        if(accountEncoded == null || accountEncoded.isEmpty()) throw new IllegalArgumentException("accountEncoded: " + accountEncoded);
        if(bucket == null || bucket.isEmpty()) throw new IllegalArgumentException("bucket: " + bucket);
        if(objectNameEncoded == null || objectNameEncoded.isEmpty()) throw new IllegalArgumentException("objectNameEncoded: " + objectNameEncoded);


        try {
            String basePathStr = new String(Base64.getUrlDecoder().decode(accountEncoded), "UTF8");
            Path basePath = Paths.get(basePathStr);
            String objectName = new String(Base64.getUrlDecoder().decode(accountEncoded), "UTF8");
            return new PathFileReference(basePath, bucket, objectName);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("", e);
        }
    }

    public static PathFileReference from(Path baseFolder, String bucket, String key) {
        return new PathFileReference(baseFolder, bucket, key);
    }

    /***************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/


    private final Path base;
    private final String bucket;
    private final String objectName;

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/


    public PathFileReference(Path base, String bucket, String objectName) {

        if(base == null) throw new IllegalArgumentException("base");
        if(bucket == null || bucket.isEmpty()) throw new IllegalArgumentException("bucket: " + bucket);
        if(objectName == null || objectName.isEmpty()) throw new IllegalArgumentException("objectName: " + objectName);

        this.base = base;
        this.bucket = bucket;
        this.objectName = objectName;
    }

    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/


    public Path getPath(){
        return base.resolve(bucket).resolve(objectName);
    }

    public String toRelativeTempUrl(){
        String accoutEnc = null;
        try {
            accoutEnc = Base64.getUrlEncoder().encodeToString(base.toString().getBytes("UTF8"));
            String objectNameEnc = Base64.getUrlEncoder().encodeToString(objectName.getBytes("UTF8"));
            return String.format("/josc/%s/buckets/%s/%s", accoutEnc, bucket, objectNameEnc);
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException("", e);
        }
    }


    @Override
    public String toString() {
        return "PathFileReference{" +
                "base=" + base +
                ", bucket='" + bucket + '\'' +
                ", objectName='" + objectName + '\'' +
                '}';
    }
}
