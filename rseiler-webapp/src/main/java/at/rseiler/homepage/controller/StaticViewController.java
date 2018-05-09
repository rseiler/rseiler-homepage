package at.rseiler.homepage.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ViewResolver;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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

    private static final Logger LOGGER = LoggerFactory.getLogger(StaticViewController.class);
    private final ViewResolver viewResolver;
    private final Map<String, Boolean> viewExists = new HashMap<>();

    @Autowired
    public StaticViewController(ViewResolver viewResolver) {
        this.viewResolver = viewResolver;
    }

    /**
     * Handles generic static pages.
     *
     * @param page the name of the static page
     * @return the view name
     */
    @RequestMapping("/{page:[\\w-]+}")
    public String generic(@PathVariable("page") String page) {
        return getViewName(page.toLowerCase(Locale.ENGLISH));
    }

    /**
     * Returns the view name based on the page if it exists. Otherwise the 404-not-found view name.
     *
     * @param page the name of the page
     * @return the view name.
     */
    private String getViewName(String page) {
        String staticPage = "page/static/" + page;
        return viewExists(staticPage) ? staticPage : "page/static/404-not-found";
    }

    /**
     * Checks if the view exists.
     *
     * @param view the view which should be checked
     * @return true if the view exists
     */
    private boolean viewExists(String view) {
        if (!viewExists.containsKey(view)) {
            try {
                viewExists.put(view, viewResolver.resolveViewName(view, Locale.ENGLISH) != null);
            } catch (Exception e) {
                LOGGER.trace("View \"{}\" doesn't exist", view, e);
            }
        }

        return viewExists.get(view);
    }
}
