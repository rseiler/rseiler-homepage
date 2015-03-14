package at.rseiler.homepage.interceptor;

import at.rseiler.homepage.service.ReleaseInfoService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Injects global data into the modelMap.
 *
 * @author Reinhard Seiler {@literal <rseiler.developer@gmail.com>}
 */
@Service
public class DataInterceptor extends HandlerInterceptorAdapter {

    private final ReleaseInfoService releaseInfoService;

    @Autowired
    public DataInterceptor(ReleaseInfoService releaseInfoService) {
        this.releaseInfoService = releaseInfoService;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        if (modelAndView != null) {
            addViewName(modelAndView);
            addHostUri(request, modelAndView);
            addCanonicalUrl(request, modelAndView);
            addReleaseInfo(modelAndView);
            addTitleTag(modelAndView);
        }
    }

    /**
     * Injects the view name as "pageName" into the modelMap.
     * Replaces all slashes (/) with minuses (-) from the viewName.
     *
     * @param modelAndView the model into the date will be added
     */
    private void addViewName(ModelAndView modelAndView) {
        modelAndView.addObject("pageName", StringUtils.replaceChars(modelAndView.getViewName(), '/', '-'));
    }

    /**
     * Injects the view name as "hostUri" into the modelMap.
     *
     * @param request      the current request object
     * @param modelAndView the model into the date will be added
     */
    private void addHostUri(HttpServletRequest request, ModelAndView modelAndView) {
        modelAndView.addObject("hostUri", request.getRemoteHost());
    }

    private void addCanonicalUrl(HttpServletRequest request, ModelAndView modelAndView) {
        Pattern p = Pattern.compile("http://([\\w\\.\\d:]+)");
        Matcher m = p.matcher(request.getRequestURL().toString());
        if (m.find()) {
            modelAndView.addObject("canonicalUrl", request.getRequestURL().toString().replace(m.group(1), "rseiler.at"));
        }
    }

    /**
     * Injects the data from the release.info
     *
     * @param modelAndView the model into the date will be added
     */
    private void addReleaseInfo(ModelAndView modelAndView) {
        modelAndView.addObject("buildTime", releaseInfoService.getBuildTime());
        modelAndView.addObject("buildVersion", releaseInfoService.getBuildVersion());
    }

    private void addTitleTag(ModelAndView modelAndView) {
        if (!modelAndView.getModelMap().containsAttribute("titleTag")) {
            modelAndView.addObject("titleTag", "div");
        }
    }

}
