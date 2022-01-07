package com.github.jfcloud.jos;

import com.github.jfcloud.common.feign.annotation.EnableJfcloudFeignClients;
import com.github.jfcloud.common.security.annotation.EnableJfcloudResourceServer;
import com.github.jfcloud.common.swagger.annotation.EnableJfcloudSwagger2;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableJfcloudSwagger2
@EnableJfcloudFeignClients
@EnableJfcloudResourceServer
@EnableDiscoveryClient
@SpringBootApplication
@MapperScan("com.github.jfcloud.jos.mapper")
public class JfcloudJosApplication {

    public static void main(String[] args) {
        SpringApplication.run(com.github.jfcloud.jos.JfcloudJosApplication.class, args);
    }

}
