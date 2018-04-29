package com.elderbyte.josc.driver.fs.config;

import com.elderbyte.josc.core.JoscDriverManager;
import com.elderbyte.josc.driver.fs.FsObjectController;
import com.elderbyte.josc.driver.fs.JoscDriverFS;
import com.elderbyte.josc.spring.support.DefaultLocalHostUrlProvider;
import com.elderbyte.josc.spring.support.LocalHostUrlProvider;
import com.elderbyte.josc.spring.support.streaming.DefaultMimetypeProvider;
import com.elderbyte.josc.spring.support.streaming.MimeTypeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class JoscFsDriverAutoConfiguration implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(JoscFsDriverAutoConfiguration.class);


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

    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("Configuring Josc FS driver ...");

        JoscDriverManager.getDefault().register(
                new JoscDriverFS(hostUrlProvider)
        );
    }
}
