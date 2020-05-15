package com.example.demo.cfg;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import java.util.Properties;

import com.github.pagehelper.PageHelper;

@Configuration
public class PageHelperCfg {
    //配置mybatis的分页插件pageHelper    
    @Bean
    public PageHelper pageHelper(){
        PageHelper pageHelper = new PageHelper();
        Properties properties = new Properties();
        properties.setProperty("offsetAsPageNum","true");
        properties.setProperty("rowBoundsWithCount","true");
        properties.setProperty("reasonable","true");
        properties.setProperty("dialect","mysql");    //配置mysql数据库的方言
        pageHelper.setProperties(properties);
        return pageHelper;
    }
}