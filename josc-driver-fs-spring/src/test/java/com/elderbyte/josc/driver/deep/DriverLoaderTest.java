package com.elderbyte.josc.driver.deep;

import com.elderbyte.josc.api.JoscConnectionProperties;
import com.elderbyte.josc.core.JoscDriverManager;
import com.elderbyte.josc.driver.fs.JoscDriverFS;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

@SpringBootTest(classes = SpringBootTestApp.class)
@RunWith(SpringJUnit4ClassRunner.class)
public class DriverLoaderTest {

    @Test
    public void testServiceLoader(){
        var driverManager = JoscDriverManager.getDefault();

        var drivers = new ArrayList<>(driverManager.getDrivers());

        Assert.assertEquals(1, drivers.size());
        var driver = drivers.get(0);
        Assert.assertTrue(driver instanceof JoscDriverFS);
    }

    @Test
    public void testFSDriverAvailable(){
        var driverManager = JoscDriverManager.getDefault();
        var client = driverManager.buildClient("josc:fs:/test", new JoscConnectionProperties());
        Assert.assertNotNull(client);
    }
}
