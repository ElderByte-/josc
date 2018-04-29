package com.elderbyte.josc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;

import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * Created by isnull on 28.03.17.
 */
@SpringBootApplication
public class SpringBootTestApp {

    /**
     * Main method, used to run the application.
     */
    public static void main(String[] args) throws UnknownHostException, SocketException {
        SpringApplication app = new SpringApplication(SpringBootTestApp.class);

        // Check if the selected profile has been set as argument.
        // if not the development profile will be added
        Environment env = app.run(args).getEnvironment();
    }


}
