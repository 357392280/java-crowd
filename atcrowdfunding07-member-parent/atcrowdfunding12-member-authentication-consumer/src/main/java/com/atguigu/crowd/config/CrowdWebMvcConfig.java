package com.atguigu.crowd.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CrowdWebMvcConfig implements WebMvcConfigurer {



    public void addViewControllers(ViewControllerRegistry registry) {
        String urlPath="/auth/member/to/reg/page";

        String viewName="reg";

        registry.addViewController(urlPath).setViewName(viewName);
        registry.addViewController("/auth/member/to/login/page").setViewName("login");
        registry.addViewController("/auth/member/to/center/page").setViewName("member-center");
    }
}
