package com.example.frontwebmvc.common.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.example.frontwebmvc.domain.repository")
public class MyBatisConfig {
    
}
