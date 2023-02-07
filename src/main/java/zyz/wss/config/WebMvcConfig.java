package zyz.wss.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import zyz.wss.interceptor.LoginRequestInterceptor;
import zyz.wss.interceptor.PassportInterceptor;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Autowired
    private PassportInterceptor pi;

    @Autowired 
    private LoginRequestInterceptor lri;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        registry.addResourceHandler("/upload/**").addResourceLocations("file:D:/resource/upload/");
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(pi)
            .excludePathPatterns("/error", "/static/**");
        registry.addInterceptor(lri)
                .excludePathPatterns("/error", "/static/**",
                 "/reglogin", "/user/**");
        WebMvcConfigurer.super.addInterceptors(registry);
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/reglogin");
        registry.addViewController("/uploadview");
        WebMvcConfigurer.super.addViewControllers(registry);
    }
}