package com.elderbyte.josc.api;


import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class JoscConnectionProperties {

    /***************************************************************************
     *                                                                         *
     * Fields                                                                  *
     *                                                                         *
     **************************************************************************/

    private final Map<String, String> properties = new HashMap<>();

    /***************************************************************************
     *                                                                         *
     * Constructor                                                             *
     *                                                                         *
     **************************************************************************/

    public JoscConnectionProperties(){ }

    public JoscConnectionProperties(JoscConnectionProperties properties){
        this(properties.properties);
    }

    public JoscConnectionProperties(Map<String, String> properties){
        this.properties.putAll(properties);
    }

    /***************************************************************************
     *                                                                         *
     * Properties                                                              *
     *                                                                         *
     **************************************************************************/

    public String getRequiredProperty(String key){
        return getProperty(key).orElseThrow(() -> new IllegalStateException("The required property '" + key + "' was not found!"));
    }

    public Optional<String> getProperty(String key){
        return Optional.ofNullable(properties.get(key));
    }

    public Map<String, String> getMapSnapshot() {
        return new HashMap<>(properties);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        JoscConnectionProperties that = (JoscConnectionProperties) o;

        return properties.equals(that.properties);
    }

    @Override
    public int hashCode() {
        return properties.hashCode();
    }
}
