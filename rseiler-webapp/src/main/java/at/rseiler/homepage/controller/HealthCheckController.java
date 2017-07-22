package at.rseiler.homepage.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handles request to the health check.
 *
 * @author Reinhard Seiler {@literal <rseiler.developer@gmail.com>}
 */
@Controller
public class HealthCheckController {

    /**
     * Returns the current timestamp in milliseconds.
     *
     * @return the view name
     */
    @ResponseBody
    @RequestMapping("/health-check")
    public String root() {
        return String.valueOf(System.currentTimeMillis());
    }

}
