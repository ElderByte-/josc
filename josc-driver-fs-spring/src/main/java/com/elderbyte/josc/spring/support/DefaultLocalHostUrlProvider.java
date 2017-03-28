package com.elderbyte.josc.spring.support;


import com.elderbyte.josc.api.ObjectStoreConfigurationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import java.util.Optional;

public class DefaultLocalHostUrlProvider implements LocalHostUrlProvider {

    @Autowired
    private Environment env;

    @Override
    public String getLocalHostUrl() {
        return buildLocalHostUrl()
                .orElseThrow(() -> new ObjectStoreConfigurationException("Could not automatically determine a public IP / host for local object-store emulation."));
    }

    private Optional<String> buildLocalHostUrl(){
        try {
            return LanInetAddressUtil.getPublicIp()
                    .map(address -> "http://"+ address.getHostAddress()+":" + env.getProperty("server.port"));
        } catch (Exception e) {
            throw new ObjectStoreConfigurationException("Failed to get fs emulation host url.", e);
        }
    }
}
