package com.elderbyte.josc.driver;

import com.elderbyte.josc.SpringBootTestApp;
import com.elderbyte.josc.core.JoscDriverManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@SpringBootTest(classes = SpringBootTestApp.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class DriverLoaderTest {

    @Test
    public void testServiceLoader(){
        var driverManager = JoscDriverManager.getDefault();
        Assert.assertEquals(1, driverManager.getDrivers().size());
    }
}
