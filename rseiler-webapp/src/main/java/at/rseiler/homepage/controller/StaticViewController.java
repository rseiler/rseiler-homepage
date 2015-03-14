package at.rseiler.homepage.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.view.AbstractTemplateViewResolver;

import javax.servlet.http.HttpServletRequest;

/**
 * Handles requests to static pages.
 * <p>
 * Static pages are pages which are located in the WEB-INF/velocity/page/static/ folder. They don't need additional
 * data from the controller and therefore can be called with this generic controller.
 *
 * @author Reinhard Seiler {@literal <rseiler.developer@gmail.com>}
 */
@Controller
public class StaticViewController {

    private final AbstractTemplateViewResolver viewResolver;
    private final LocaleResolver localeResolver;

    @Autowired
    public StaticViewController(AbstractTemplateViewResolver viewResolver, LocaleResolver localeResolver) {
        this.viewResolver = viewResolver;
        this.localeResolver = localeResolver;
    }

    /**
     * Handles generic static pages.
     *
     * @param request the current request object
     * @param page    the name of the static page
     * @return the view name
     */
    @RequestMapping("/{page:[\\w-]+}")
    public String generic(HttpServletRequest request, @PathVariable("page") String page) {
        return getViewName(request, page.toLowerCase());
    }

    /**
     * Returns the view name based on the page if it exists. Otherwise the 404-not-found view name.
     *
     * @param request the current request object
     * @param page    the name of the page
     * @return the view name.
     */
    private String getViewName(HttpServletRequest request, String page) {
        String staticPage = "static/" + page;
        return viewExists(request, staticPage) ? staticPage : "static/404-not-found";
    }

    /**
     * Checks if the view exists.
     *
     * @param request the current request object
     * @param view    the view which should be checked
     * @return true if the view exists
     */
    private boolean viewExists(HttpServletRequest request, String view) {
        try {
            return viewResolver.resolveViewName(view, localeResolver.resolveLocale(request)) != null;
        } catch (Exception ignore) {
        }
        return false;
    }

}
