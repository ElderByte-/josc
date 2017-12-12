package com.elderbyte.josc.driver.fs;

import com.elderbyte.josc.api.BlobObject;
import org.junit.Test;

import java.nio.file.Paths;

import static org.junit.Assert.*;

public class PathBlobObjectBuilderTest {

    @Test
    public void from_folder() {
        BlobObject blobObject = PathBlobObjectBuilder.from(Paths.get("/root/bucket/my/path"), Paths.get("/root/bucket"));
        assertEquals("my/path", blobObject.getObjectName());
        assertEquals(null, blobObject.getVirtualExtension());
    }

    @Test
    public void from_file() {
        BlobObject blobObject = PathBlobObjectBuilder.from(Paths.get("/root/bucket/my/path/text.txt"), Paths.get("/root/bucket"));
        assertEquals("my/path/text.txt", blobObject.getObjectName());
        assertEquals("text.txt", blobObject.getVirtualFileName());
        assertEquals(".txt", blobObject.getVirtualExtension());
    }

    @Test
    public void from_file_special() {
        BlobObject blobObject = PathBlobObjectBuilder.from(Paths.get("/Users/isnull/Movies/.localized"), Paths.get("/Users/isnull/Movies"));
        assertEquals(".localized", blobObject.getObjectName());
        assertEquals(".localized", blobObject.getVirtualFileName());
        assertEquals(null, blobObject.getVirtualExtension());
    }
}