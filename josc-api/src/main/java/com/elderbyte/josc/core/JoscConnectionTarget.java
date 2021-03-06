package com.elderbyte.josc.core;

import com.elderbyte.josc.api.JoscConnectionProperties;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents a josc connection target.
 */
public class JoscConnectionTarget {

    /***************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/

    private final String protocol;
    private final String host;
    private final Map<String, String> properties = new HashMap<>();


    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public JoscConnectionTarget(String protocol, String host, Map<String,String> properties){
        this.protocol = protocol;
        this.host = host;
        this.properties.putAll(properties);
    }

    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    /**
     * Gets the josc sub protocol, such as s3, minio, webdav or fs (file system)
     */
    public String getProtocol() {
        return protocol;
    }

    /**
     * Gets the host. This is usually a domain name and optionally a port, but is basically
     * up to the sub-protocol driver. For example, if you use the fs driver for local file system
     * access, the host is the base folder on the local host.
     *
     */
    public String getHost() {
        return host;
    }

    public JoscConnectionProperties getProperties(){
        return new JoscConnectionProperties(this.properties);
    }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

    public JoscConnectionTarget withMergedProperties(Map<String,String> newProps){
        var mergedProps = new HashMap<>(this.properties);
        mergedProps.putAll(newProps);
        return new JoscConnectionTarget(protocol, host, mergedProps);
    }

    @Override
    public String toString() {
        return "JoscConnectionTarget{" +
                "protocol='" + protocol + '\'' +
                ", host='" + host + '\'' +
                ", properties=" + properties +
                '}';
    }
}
