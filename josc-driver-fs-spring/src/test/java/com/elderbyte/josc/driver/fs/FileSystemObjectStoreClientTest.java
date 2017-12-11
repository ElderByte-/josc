package com.elderbyte.josc.driver.fs;

import com.elderbyte.josc.api.BlobObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

public class FileSystemObjectStoreClientTest {

    private FileSystemObjectStoreClient localClient;


    @Before
    public void setup(){
        localClient = new FileSystemObjectStoreClient(Paths.get(System.getenv("HOME")), "http://localhost");
    }


    @Test
    public void listBlobObjects() {


        List<BlobObject> objects =  localClient.listBlobObjects("Movies", "", false)
                .collect(Collectors.toList());

        // Assert.assertEquals(4, objects.size());
    }

    @Test
    public void listBlobObjectsRecurksive() {


        List<BlobObject> objects =  localClient.listBlobObjects("Movies", "", true)
                .collect(Collectors.toList());

        // Assert.assertEquals(7, objects.size());
    }
}