package at.rseiler.homepage.interceptor;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import javax.servlet.http.HttpServletResponse;

import static org.mockito.Mockito.*;

public class RedirectToDomainInterceptorTest {

    @Test
    public void preHandleWithDomain() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("http://rseiler.at/");
        HttpServletResponse response = mock(HttpServletResponse.class);

        new RedirectToDomainInterceptor().preHandle(request, response, null);

        verify(response, never()).setStatus(anyInt());
        verify(response, never()).setHeader(any(), any());
    }

    @Test
    public void preHandleWithLocalhost() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("http://localhost/");
        HttpServletResponse response = mock(HttpServletResponse.class);

        new RedirectToDomainInterceptor().preHandle(request, response, null);

        verify(response, never()).setStatus(anyInt());
        verify(response, never()).setHeader(any(), any());
    }

    @Test
    public void preHandleWithIp() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI("http://84.113.117.224/");
        HttpServletResponse response = mock(HttpServletResponse.class);

        new RedirectToDomainInterceptor().preHandle(request, response, null);

        verify(response).setStatus(eq(HttpServletResponse.SC_MOVED_PERMANENTLY));
        verify(response).setHeader(eq("Location"), eq("http://rseiler.at/"));
    }

}