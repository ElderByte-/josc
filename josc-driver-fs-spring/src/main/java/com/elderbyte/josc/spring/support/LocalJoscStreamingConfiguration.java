package com.elderbyte.josc.spring.support;

import com.elderbyte.josc.driver.fs.FsObjectController;
import com.elderbyte.josc.spring.support.streaming.DefaultMimetypeProvider;
import com.elderbyte.josc.spring.support.streaming.MimeTypeProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LocalJoscStreamingConfiguration {

    @Bean
    @ConditionalOnMissingBean(MimeTypeProvider.class)
    public MimeTypeProvider mimetypeProvider(){
        return new DefaultMimetypeProvider();
    }

    @Bean
    public FsObjectController fsObjectController(){
        return new FsObjectController();
    }

}
