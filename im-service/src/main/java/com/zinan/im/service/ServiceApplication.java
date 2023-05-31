package com.zinan.im.service;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author lzn
 * @date 2023/05/30 20:51
 * @description
 */
@SpringBootApplication(scanBasePackages = {"com.zinan.im.service", "com.zinan.im.common"})
@MapperScan("com.zinan.im.service.*.dao.mapper")
public class ServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ServiceApplication.class, args);
    }

}
