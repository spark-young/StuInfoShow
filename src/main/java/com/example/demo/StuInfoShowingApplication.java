package com.example.demo;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;



@SpringBootApplication
@MapperScan("com.example.demo.mapper")//使用MapperScan可以自动加载指定路径下的Dao，避免手动注解每一个Dao类	
public class StuInfoShowingApplication {
	public static void main(String[] args) {
		SpringApplication.run(StuInfoShowingApplication.class, args);
	}
}
