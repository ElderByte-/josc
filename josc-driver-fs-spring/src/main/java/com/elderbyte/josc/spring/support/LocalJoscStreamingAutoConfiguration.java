package com.elderbyte.josc.spring.support;

import com.elderbyte.josc.core.JoscDriverManager;
import com.elderbyte.josc.driver.fs.FsObjectController;
import com.elderbyte.josc.driver.fs.JoscDriverFS;
import com.elderbyte.josc.spring.support.streaming.DefaultMimetypeProvider;
import com.elderbyte.josc.spring.support.streaming.MimeTypeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class LocalJoscStreamingAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(LocalJoscStreamingAutoConfiguration.class);


    @Autowired
    private LocalHostUrlProvider hostUrlProvider;


    @Bean
    @ConditionalOnMissingBean(MimeTypeProvider.class)
    public MimeTypeProvider mimetypeProvider(){
        return new DefaultMimetypeProvider();
    }

    @Bean
    @ConditionalOnMissingBean(LocalHostUrlProvider.class)
    public LocalHostUrlProvider localHostUrlProvider(){ return new DefaultLocalHostUrlProvider();}

    @Bean
    public FsObjectController fsObjectController(){
        return new FsObjectController();
    }


    @PostConstruct
    public void configureJosc(){

        log.info("Configuring Josc FS driver ...");

        JoscDriverManager.getDefault().register(
                new JoscDriverFS(hostUrlProvider)
        );
    }
}
