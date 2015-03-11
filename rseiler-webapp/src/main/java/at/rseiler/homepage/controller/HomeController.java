package at.rseiler.homepage.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Handles request to the root context.
 *
 * @author Reinhard Seiler {@literal <rseiler.developer@gmail.com>}
 */
@Controller
public class HomeController {

    /**
     * Handles request to the root context.
     *
     * @return the view name
     */
    @RequestMapping("/")
    public String root() {
        return "home";
    }

}
