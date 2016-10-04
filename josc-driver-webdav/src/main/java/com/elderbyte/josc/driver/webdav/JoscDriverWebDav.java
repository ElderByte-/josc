package com.elderbyte.josc.driver.webdav;

import com.elderbyte.josc.api.JoscConnectionProperties;
import com.elderbyte.josc.api.ObjectStoreClient;
import com.elderbyte.josc.api.ObjectStoreConnectionException;
import com.elderbyte.josc.api.JoscDriver;
import com.github.sardine.Sardine;
import com.github.sardine.SardineFactory;

/**
 * Created by isnull on 18/09/16.
 */
public class JoscDriverWebDav implements JoscDriver {


    @Override
    public ObjectStoreClient openConnection(String host, JoscConnectionProperties properties) throws ObjectStoreConnectionException {

        try {
            String account = properties.getRequiredProperty("user");
            String key = properties.getRequiredProperty("pass");
            Sardine sardine = SardineFactory.begin(account, key);
            sardine.list(host); // Test connection

            return new WebDavObjectStoreClient(sardine, host, account, key);
        }catch (Exception e){
            throw new ObjectStoreConnectionException("Failed to open connection to webdav object store!", e);
        }
    }

    @Override
    public boolean supports(String protocol, String host, JoscConnectionProperties properties) {
        return "webdav".equalsIgnoreCase(protocol);
    }
}
