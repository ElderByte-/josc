package com.elderbyte.josc;

import com.elderbyte.josc.core.JoscDriverManager;
import org.junit.Assert;
import org.junit.Test;

public class DriverLoaderTest {

    @Test
    public void testServiceLoader(){
        var driverManager = JoscDriverManager.getDefault();
        Assert.assertEquals(3, driverManager.getDrivers().size());
    }

}
