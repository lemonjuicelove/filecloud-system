package com.github.jfcloud.jos.core.autoconfiguration;

import com.github.jfcloud.jos.core.factory.FileOperatorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@EnableConfigurationProperties({JosCoreFileConfProperties.class})
public class JosCoreFileAutoConfiguration {

    @Autowired
    private  JosCoreFileConfProperties josCoreFileConfProperties;

    @Bean
    public FileOperatorFactory fileOperatorFactory(){
        return new FileOperatorFactory(josCoreFileConfProperties);
    }

}
