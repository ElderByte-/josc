package com.elderbyte.josc.driver.fs;

import org.junit.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.Assert.*;

public class PathRelativizerTest {

    @Test
    public void relativize() {
        Path relative = PathRelativizer.relativize(Paths.get("/root/abc/and/sub"), Paths.get("/root/abc"));
        assertEquals(Paths.get("and/sub"), relative);
    }

    @Test
    public void relativize2() {
        Path relative = PathRelativizer.relativize(Paths.get("/root/abc/and/sub/.localize"), Paths.get("/root/abc"));
        assertEquals(Paths.get("and/sub/.localize"), relative);
    }


    @Test(expected = IllegalArgumentException.class)
    public void relativize_error() {
        Path relative = PathRelativizer.relativize(Paths.get("/root/abc/and/sub/.localize"), Paths.get("/root/abcz"));
    }
}