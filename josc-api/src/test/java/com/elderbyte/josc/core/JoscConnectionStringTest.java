package com.elderbyte.josc.core;


import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;

public class JoscConnectionStringTest {

    @Test
    public void testSimple(){
        var cs = JoscConnectionString.parse("josc:s3:http://myserver.com:9000");

        Assert.assertEquals("http://myserver.com:9000", cs.getHost());
        Assert.assertEquals("s3", cs.getProtocol());
    }

    @Test
    public void testVendorProperties(){
        var cs = JoscConnectionString.parse("josc:s3:http://myserver.com:9000;some=prop;another=myprop");

        Assert.assertEquals("http://myserver.com:9000", cs.getHost());
        Assert.assertEquals("s3", cs.getProtocol());
        Assert.assertEquals("prop", cs.getProperties().getRequiredProperty("some"));
        Assert.assertEquals("myprop", cs.getProperties().getRequiredProperty("another"));
    }

    @Test
    public void testVendorPropertiesMergeEmptyProps() {
        var cs = JoscConnectionString.parse("josc:s3:http://myserver.com:9000;some=prop;another=myprop");

        var props = new HashMap<String, String>();

        cs = cs.withMergedProperties(props);

        Assert.assertEquals("http://myserver.com:9000", cs.getHost());
        Assert.assertEquals("s3", cs.getProtocol());
        Assert.assertEquals("prop", cs.getProperties().getRequiredProperty("some"));
        Assert.assertEquals("myprop", cs.getProperties().getRequiredProperty("another"));

    }

    @Test
    public void testVendorPropertiesMergeProps() {
        var cs = JoscConnectionString.parse("josc:s3:http://myserver.com:9000;key1=prop1;key2=prop2");

        var props = new HashMap<String, String>();
        props.put("key3", "prop3");

        cs = cs.withMergedProperties(props);

        Assert.assertEquals("http://myserver.com:9000", cs.getHost());
        Assert.assertEquals("s3", cs.getProtocol());
        Assert.assertEquals("prop1", cs.getProperties().getRequiredProperty("key1"));
        Assert.assertEquals("prop2", cs.getProperties().getRequiredProperty("key2"));
        Assert.assertEquals("prop3", cs.getProperties().getRequiredProperty("key3"));

    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingJsocFail(){
        JoscConnectionString.parse("s3:http://myserver.com:9000");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testMissingProtocolFail(){
        JoscConnectionString.parse("josc://myserver.com:9000");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullFail(){
        JoscConnectionString.parse(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testEmptyFail(){
        JoscConnectionString.parse("");
    }

}
