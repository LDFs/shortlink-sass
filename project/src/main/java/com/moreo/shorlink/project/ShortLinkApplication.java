package com.moreo.shorlink.project;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 启动项
 * 注解 MapperScan， MapperScan 会自动扫描该路径下的 Mapper
 */
@SpringBootApplication
@MapperScan("com.moreo.shorlink.project.dao.mapper")
public class ShortLinkApplication {
    public static void main(String[] args) {
        SpringApplication.run(ShortLinkApplication.class, args);
    }
}
