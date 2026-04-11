package com.campus.campusbao.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置商品图静态资源
        registry.addResourceHandler("/products/**")
                .addResourceLocations("classpath:/static/products/");

        // 配置头像静态资源 - 改成文件路径
        registry.addResourceHandler("/avatar/**")
                .addResourceLocations("file:./src/main/resources/static/avatar/");
        registry.addResourceHandler("/need/**")
                .addResourceLocations("file:./src/main/resources/static/need/");
    }

}