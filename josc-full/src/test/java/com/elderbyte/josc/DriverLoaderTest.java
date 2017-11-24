package com.elderbyte.josc;

import com.elderbyte.josc.core.JoscDriverManager;
import org.junit.Assert;
import org.junit.Test;

public class DriverLoaderTest {

    @Test
    public void testServiceLoader(){
        JoscDriverManager driverManager = JoscDriverManager.getDefault();
        Assert.assertEquals(4, driverManager.getDrivers().size());
    }

}
