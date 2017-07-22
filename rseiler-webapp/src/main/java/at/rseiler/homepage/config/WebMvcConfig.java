package at.rseiler.homepage.config;

import at.rseiler.homepage.interceptor.DataInterceptor;
import at.rseiler.homepage.interceptor.RedirectToDomainInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import java.util.Arrays;
import java.util.List;

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    private final DataInterceptor dataInterceptor;
    private final RedirectToDomainInterceptor redirectToDomainInterceptor;

    @Autowired
    public WebMvcConfig(DataInterceptor dataInterceptor, RedirectToDomainInterceptor redirectToDomainInterceptor) {
        this.dataInterceptor = dataInterceptor;
        this.redirectToDomainInterceptor = redirectToDomainInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        getInterceptors().forEach(registry::addInterceptor);
    }

    private List<HandlerInterceptorAdapter> getInterceptors() {
        return Arrays.asList(
                redirectToDomainInterceptor,
                dataInterceptor
        );
    }

}
