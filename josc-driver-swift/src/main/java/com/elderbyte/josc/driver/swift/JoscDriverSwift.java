package com.elderbyte.josc.driver.swift;


import com.elderbyte.josc.api.JoscConnectionProperties;
import com.elderbyte.josc.api.JoscDriver;
import com.elderbyte.josc.api.ObjectStoreClient;
import com.elderbyte.josc.api.ObjectStoreConnectionException;
import org.javaswift.joss.client.factory.AccountConfig;
import org.javaswift.joss.client.factory.AccountFactory;
import org.javaswift.joss.client.factory.AuthenticationMethod;
import org.javaswift.joss.client.factory.TempUrlHashPrefixSource;
import org.javaswift.joss.model.Account;


public class JoscDriverSwift implements JoscDriver {

    @Override
    public ObjectStoreClient openConnection(String host, JoscConnectionProperties properties) throws ObjectStoreConnectionException {

        try {

            AccountConfig config = new AccountConfig();
            config.setAuthenticationMethod(AuthenticationMethod.BASIC);
            config.setUsername(properties.getRequiredProperty("user"));
            config.setPassword(properties.getRequiredProperty("pass"));
            config.setTempUrlHashPrefixSource(TempUrlHashPrefixSource.INTERNAL_URL_PATH);

            //config.setAuthUrl("http://myhost:8010/auth/v1.0");
            //config.setHashPassword("test");
            //config.setTenantId("huhu");
            //config.setTenantName(tenantName);

            Account account = new AccountFactory(config).createAccount();

            return new SwiftObjectStoreClient(account);
        }catch (Exception e){
            throw new ObjectStoreConnectionException("Failed to open connection to Swift (joss) object store!", e);
        }
    }

    @Override
    public boolean supports(String protocol, String host, JoscConnectionProperties properties) {
        return "swift".equalsIgnoreCase(protocol) || "joss".equalsIgnoreCase(protocol);
    }
}
