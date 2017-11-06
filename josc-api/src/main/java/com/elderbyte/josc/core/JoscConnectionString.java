package com.elderbyte.josc.core;

import com.elderbyte.josc.api.JoscConnectionProperties;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Represents a jsoc connection string
 *
 *
 * josc:{driver}://{host}[;{key:value}...]
 *
 * josc:s3://s3.amazonaws.com
 *
 */
public class JoscConnectionString {


    private static Pattern connectionStringPattern = Pattern.compile("^josc:([a-zA-Z0-9]+):([^;]+)(.*)");

    /**
     * Parses a connectionstring in the form
     *
     * josc:s3:http://aws.zone.com:80;user=foo;pass=bar;create=true
     *
     * @param connectionString The connection string to parse.
     * @return An object holding the parsed connection values.
     * @throws IllegalArgumentException Thrown when the connection string did not have a valid format.
     */
    public static JoscConnectionString parse(String connectionString) throws IllegalArgumentException {

        if (connectionString == null) throw new IllegalArgumentException("Argument connectionString must not be null!");
        if (connectionString.trim().isEmpty()) throw new IllegalArgumentException("Argument connectionString must not be empty!");

        Matcher matcher = connectionStringPattern.matcher(connectionString);

        if(matcher.matches()){

            String joscProtocol = matcher.group(1);
            String joscHostWithPort = matcher.group(2);
            String joscVendorProperties = matcher.group(3);

            return new JoscConnectionString(joscProtocol, joscHostWithPort, parseVendorProperties(joscVendorProperties));
        }else{
            throw new IllegalArgumentException("The given string is not a valid josc connection-string! " + connectionString);
        }
    }

    private static Map<String, String> parseVendorProperties(String joscVendorProperties){
        String[] hostProps = joscVendorProperties.split(";");
        Map<String, String> properties = new HashMap<>();

        for(int i=0; i < hostProps.length;i++) {
            String propertyRow = hostProps[i].trim();

            if(!propertyRow.isEmpty()){
                String[] kv = propertyRow.split("=", 2);
                if(kv.length == 2){
                    properties.put(kv[0], kv[1]);
                }else{
                    throw  new IllegalArgumentException("Malformed josc property: '" + propertyRow + "' -> a'=' is required as delemiter. " + kv.length);
                }
            }
        }

        return properties;
    }

    /***************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/

    private String protocol;
    private String host;
    private final Map<String, String> properties = new HashMap<>();


    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public JoscConnectionString(String protocol, String host, Map<String,String> properties){
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

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    /**
     * Gets the host. This is usually a domain name and optionally a port, but is basically
     * up to the sub-protocol driver. For example, if you use the fs driver for local file system
     * access, the host is the base folder on the local host.
     *
     * @return
     */
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void addProperties(Map<String, String> props){
        properties.putAll(props);
    }

    public JoscConnectionProperties getProperties(){
        return new JoscConnectionProperties(this.properties);
    }


}
