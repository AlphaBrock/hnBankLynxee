package com.sino.hnbank.screen;

import com.sino.hnbank.screen.quartz.ScreenData;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
//将项目中对应的mapper类的路径加进来就可以了
@MapperScan("com.sino.hnbank.screen.mapper")
public class HainanScreenApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        ScreenData.init(args[0]);
        SpringApplication.run(HainanScreenApplication.class, args);
    }
    @Override//为了打包springboot项目
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder builder) {
        return builder.sources(this.getClass()).logStartupInfo(false);
    }
}

