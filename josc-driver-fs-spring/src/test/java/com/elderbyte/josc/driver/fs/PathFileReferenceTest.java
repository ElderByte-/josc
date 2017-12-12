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



    @Test
    public void parse_2() {
        PathFileReference reference = PathFileReference.from(Paths.get("/hello/world"), "bucket", "cache/thumbs/8b00728a0054a05aa44e1f77be4850bb375a6422/0.28800000000000003.jpg");
        String url = reference.toRelativeTempUrl();
        Assert.assertEquals("/josc/L2hlbGxvL3dvcmxk/buckets/bucket/Y2FjaGUvdGh1bWJzLzhiMDA3MjhhMDA1NGEwNWFhNDRlMWY3N2JlNDg1MGJiMzc1YTY0MjIvMC4yODgwMDAwMDAwMDAwMDAwMy5qcGc=", reference.toRelativeTempUrl());


        PathFileReference parsed = PathFileReference.parse("L2hlbGxvL3dvcmxk", "bucket", "Y2FjaGUvdGh1bWJzLzhiMDA3MjhhMDA1NGEwNWFhNDRlMWY3N2JlNDg1MGJiMzc1YTY0MjIvMC4yODgwMDAwMDAwMDAwMDAwMy5qcGc=");


        Assert.assertEquals("/hello/world/bucket/cache/thumbs/8b00728a0054a05aa44e1f77be4850bb375a6422/0.28800000000000003.jpg", parsed.getPath().toString());
    }
}