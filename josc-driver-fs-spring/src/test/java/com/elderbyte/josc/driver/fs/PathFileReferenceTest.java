package com.elderbyte.josc.driver.fs;

import org.junit.Assert;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.*;

public class PathFileReferenceTest {

    @Test
    public void parse() {
        PathFileReference reference = PathFileReference.from(Paths.get("/hello/world"), "bucket", "some/relative/key/test.txt");
    }

    @Test
    public void getPath() {
        PathFileReference reference = PathFileReference.from(Paths.get("/hello/world"), "bucket", "some/relative/key/test.txt");
        Assert.assertEquals("/hello/world/bucket/some/relative/key/test.txt", reference.getPath().toString());
    }

    @Test
    public void toRelativeTempUrl() {
        PathFileReference reference = PathFileReference.from(Paths.get("/hello/world"), "bucket", "some/relative/key/test.txt");

        Assert.assertEquals("/josc/L2hlbGxvL3dvcmxk/buckets/bucket/c29tZS9yZWxhdGl2ZS9rZXkvdGVzdC50eHQ=", reference.toRelativeTempUrl());

    }
}