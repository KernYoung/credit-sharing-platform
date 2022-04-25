package com.fanruan.platform;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;


@SpringBootApplication(scanBasePackages = "com.fanruan.*",exclude = {MultipartAutoConfiguration.class})
@MapperScan("com.fanruan.platform.mapper")
public class CreditSharingPlatformApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(CreditSharingPlatformApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(CreditSharingPlatformApplication.class);
    }

}

//public class CreditSharingPlatformApplication {
//
//    public static void main(String[] args) {
//        SpringApplication.run(CreditSharingPlatformApplication.class, args);
//    }
//}

