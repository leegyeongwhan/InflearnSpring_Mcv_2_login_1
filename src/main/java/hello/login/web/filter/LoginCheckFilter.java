package hello.login.web.filter;

import hello.login.web.SessionConst;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.PatternMatchUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.regex.Pattern;

@Slf4j
public class LoginCheckFilter implements Filter {

    private final String[] whitList = {"/", "/members/add", "/login", "/css/*"};

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String requestURI = httpRequest.getRequestURI();
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        try {
            log.info("인증 체크 필터시각{}", requestURI);
            if (isLoginCheckPath(requestURI)) {
                log.info("인증체크 로직 실행", requestURI);
                HttpSession session = httpRequest.getSession(false);
                if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
                    log.info("미인증 사용자{}", requestURI);
                    httpResponse.sendRedirect("/login?redirectURL=" + requestURI);
                    return;
                }
            }
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw e;
        } finally {
            log.info("인증 체크필터 종료{}", requestURI);
        }
    }

    /**
     * 화이트 리스트의 경우 인증체크를 하지않는다
     */

    private boolean isLoginCheckPath(String requestURI) {
        if (!PatternMatchUtils.simpleMatch(whitList, requestURI)) return true;
        else return false;
    }
}
