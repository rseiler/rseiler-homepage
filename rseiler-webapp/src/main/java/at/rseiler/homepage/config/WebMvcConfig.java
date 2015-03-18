package at.rseiler.homepage.config;

import at.rseiler.homepage.interceptor.DataInterceptor;
import at.rseiler.homepage.interceptor.RedirectToDomainInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.servlet.view.velocity.VelocityConfigurer;
import org.springframework.web.servlet.view.velocity.VelocityLayoutViewResolver;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import static org.springframework.context.annotation.ComponentScan.Filter;

/**
 * Configures Spring MVC.
 * <ul>
 * <li>configures the resource handlers</li>
 * <li>configures the interceptors</li>
 * <li>configures the Velocity template engine</li>
 * <li>loads the messages.properties</li>
 * </ul>
 */
@EnableWebMvc
@Configuration
@ComponentScan(
        basePackages = {"at.rseiler.homepage.controller", "at.rseiler.homepage.service", "at.rseiler.homepage.interceptor"},
        includeFilters = @Filter({Service.class, Controller.class, Component.class}), useDefaultFilters = false)
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    private Resource velocityConfig;
    private DataInterceptor dataInterceptor;
    private RedirectToDomainInterceptor redirectToDomainInterceptor;

    // ----------------------------------------------------------------------
    // Life-cycle methods
    // ----------------------------------------------------------------------

    @Value("/WEB-INF/velocity.properties")
    public void setVelocityConfig(Resource velocityConfig) {
        this.velocityConfig = velocityConfig;
    }

    @Autowired
    public void setDataInterceptor(DataInterceptor dataInterceptor) {
        this.dataInterceptor = dataInterceptor;
    }

    @Autowired
    public void setRedirectToDomainInterceptor(RedirectToDomainInterceptor redirectToDomainInterceptor) {
        this.redirectToDomainInterceptor = redirectToDomainInterceptor;
    }

    // ----------------------------------------------------------------------
    // Config methods
    // ----------------------------------------------------------------------

    @Override
    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
        configurer.enable();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/resources/**").addResourceLocations("resources/").setCachePeriod(7_200);
        registry.addResourceHandler("/favicon.ico").addResourceLocations("resources/").setCachePeriod(7_200);
        registry.addResourceHandler("/robots.txt").addResourceLocations("resources/").setCachePeriod(7_200);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        getInterceptors().stream().forEach(registry::addInterceptor);
    }

    @Bean
    public VelocityLayoutViewResolver templateResolver() {
        VelocityLayoutViewResolver velocityLayoutViewResolver = new VelocityLayoutViewResolver();
        velocityLayoutViewResolver.setCache(true);
        velocityLayoutViewResolver.setPrefix("page/");
        velocityLayoutViewResolver.setSuffix(".vm");
        velocityLayoutViewResolver.setLayoutUrl("layout.vm");
        velocityLayoutViewResolver.setContentType("text/html;charset=UTF-8");
        velocityLayoutViewResolver.setToolboxConfigLocation("/WEB-INF/velocity-toolbox.xml");
        return velocityLayoutViewResolver;
    }

    @Bean
    public VelocityConfigurer velocityConfigurer() {
        VelocityConfigurer velocityConfigurer = new VelocityConfigurer();
        velocityConfigurer.setConfigLocation(velocityConfig);
        velocityConfigurer.setResourceLoaderPath("/WEB-INF/velocity/");
        return velocityConfigurer;
    }

    @Bean(name = "messageSource")
    public MessageSource messageSource() {
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("WEB-INF/i18n/messages");
        messageSource.setCacheSeconds(5);
        return messageSource;
    }

    @Bean(name = "localeResolver")
    public SessionLocaleResolver localeResolver() {
        SessionLocaleResolver sessionLocaleResolver = new SessionLocaleResolver();
        sessionLocaleResolver.setDefaultLocale(Locale.GERMAN);
        return sessionLocaleResolver;
    }

    @Bean(name = "handlerMapping")
    public RequestMappingHandlerMapping configRequestMappingHandlerMapping() {
        RequestMappingHandlerMapping requestMappingHandlerMapping = new RequestMappingHandlerMapping();
        requestMappingHandlerMapping.setUseSuffixPatternMatch(false);
        requestMappingHandlerMapping.setUseTrailingSlashMatch(false);
        return requestMappingHandlerMapping;
    }

    @Bean
    public List<HandlerInterceptorAdapter> getInterceptors() {
        return Arrays.asList(
                redirectToDomainInterceptor,
                dataInterceptor
        );
    }

}
