package at.rseiler.homepage.interceptor;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class RedirectToDomainInterceptor extends HandlerInterceptorAdapter {

    private static final Pattern DOMAIN_PATTERN = Pattern.compile("http://([\\w\\.\\d:]+)");

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        Matcher m = DOMAIN_PATTERN.matcher(request.getRequestURL().toString());

        if (m.find()) {
            String domain = m.group(1);

            if (!domain.contains("rseiler.at") && !domain.contains("localhost")) {
                response.setStatus(HttpServletResponse.SC_MOVED_PERMANENTLY);
                response.setHeader("Location", getRedirectUri(request.getRequestURL().toString(), domain));
                return false;
            }
        }

        return true;
    }

    private String getRedirectUri(String url, String domain) {
        return url.replace(domain, "rseiler.at");
    }

}
