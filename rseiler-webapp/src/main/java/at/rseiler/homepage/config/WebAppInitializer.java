package at.rseiler.homepage.config;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.Filter;
import javax.servlet.ServletRegistration.Dynamic;

/**
 * Config of the webapp.
 *
 * @author Reinhard Seiler {@literal <rseiler.developer@gmail.com>}
 */
public final class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    public WebAppInitializer() {
    }

    // ----------------------------------------------------------------------
    // Config methods
    // ----------------------------------------------------------------------

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{WebMvcConfig.class};
    }

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[]{getCharacterEncodingFilter()};
    }

    @Override
    protected void customizeRegistration(Dynamic registration) {
        registration.setInitParameter("defaultHtmlEscape", "true");
    }

    // ----------------------------------------------------------------------
    // Implementation Methods
    // ----------------------------------------------------------------------

    private CharacterEncodingFilter getCharacterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);
        return characterEncodingFilter;
    }

}