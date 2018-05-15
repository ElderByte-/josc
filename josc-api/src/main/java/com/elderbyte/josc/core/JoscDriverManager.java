package com.elderbyte.josc.core;

import com.elderbyte.josc.api.*;
import com.elderbyte.josc.api.JoscDriver;

import java.util.*;

/**
 * The standard josc driver manager implementation.
 */
public class JoscDriverManager implements ObjectStoreClientFactory {

    /***************************************************************************
     *                                                                         *
     * Static Builders                                                         *
     *                                                                         *
     **************************************************************************/

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

    /***************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/

    private final List<JoscDriver> drivers = new ArrayList<>();

    /***************************************************************************
     *                                                                         *
     * Constructors                                                            *
     *                                                                         *
     **************************************************************************/

    public JoscDriverManager() { }

    /***************************************************************************
     *                                                                         *
     * Public API                                                              *
     *                                                                         *
     **************************************************************************/

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

        var cs = JoscConnectionString.parse(connectionString)
                    .withMergedProperties(properties.getMapSnapshot());

        return getDriverFor(cs);
    }

    /***************************************************************************
     *                                                                         *
     * Private methods                                                         *
     *                                                                         *
     **************************************************************************/

    private ObjectStoreClient getDriverFor(JoscConnectionTarget target){
        for (JoscDriver driver : drivers) {
            if(driver.supports(target.getProtocol(), target.getHost(), target.getProperties())){
                return driver.openConnection(target.getHost(), target.getProperties());
            }
        }
        throw new ObjectStoreConnectionException("Failed to open connection, no driver supports your protocol " + target.getProtocol());
    }


}