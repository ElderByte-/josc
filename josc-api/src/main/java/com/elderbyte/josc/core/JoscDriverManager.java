package com.elderbyte.josc.core;

import com.elderbyte.josc.api.*;
import com.elderbyte.josc.api.JoscDriver;

import java.util.*;

/**
 * The standard josc driver manager implementation.
 */
public class JoscDriverManager implements ObjectStoreClientFactory {

    private final static JoscDriverManager defaultInstance = buildDefault();

    /**
     * Gets the default driver manager, where josc drivers register themselves.
     */
    public static JoscDriverManager getDefault(){
        return defaultInstance;
    }


    private static JoscDriverManager buildDefault(){

        ServiceLoader<JoscDriver> driverLoader = ServiceLoader.load(JoscDriver.class);

        JoscDriverManager driverManager = new JoscDriverManager();

        try {
            for(JoscDriver driver : driverLoader) {
                driverManager.register(driver);
            }
        } catch (ServiceConfigurationError serviceError) {
            serviceError.printStackTrace();
        }

        return driverManager;
    }


    private final List<JoscDriver> drivers = new ArrayList<>();


    public JoscDriverManager() {

    }

    /**
     * Registers the given driver in this driver manager
     */
    public void register(JoscDriver driver){
        if(driver == null) throw new IllegalArgumentException("Argument 'driver' was null!");
        drivers.add(driver);
    }

    /**
     * Returns all registered drivers
     */
    public Collection<JoscDriver> getDrivers(){
        return new ArrayList<>(drivers);
    }

    @Override
    public ObjectStoreClient buildClient(String connectionString, JoscConnectionProperties properties) throws ObjectStoreConnectionException {

        JoscConnectionString cs = JoscConnectionString.parse(connectionString);

        cs.addProperties(properties.getMapSnapshot());

        for (JoscDriver driver : drivers) {
            if(driver.supports(cs.getProtocol(), cs.getHost(), cs.getProperties())){
                return driver.openConnection(cs.getHost(), cs.getProperties());
            }
        }
        throw new ObjectStoreConnectionException("Failed to open connection, no driver supports your protocol " + cs.getProtocol());
    }
}