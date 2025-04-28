package com.nie.common.interceptor;

import com.nie.common.tools.UserContest;
import com.nie.common.tools.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
@Component
public class UserInfoInterceptor implements HandlerInterceptor {
    public static ThreadLocal<UserInfo> userInfoThreadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("intercepter thread ID: " + Thread.currentThread().getId());
        String s = request.getHeader("userid");
        if (s != null && !s.isEmpty()) {
            Integer userid = Integer.valueOf(s);
            UserContest.setUserId(userid);
            log.info("uTest : {}", UserContest.getUserId());
        }
        final HttpSession httpSession = request.getSession();
        SecurityContext securityContext = (SecurityContext) httpSession.getAttribute("SPRING_SECURITY_CONTEXT");
        final UserInfo userInfo = new UserInfo();
        if (securityContext != null) {
            Authentication authentication = securityContext.getAuthentication();
            if (authentication != null) {
                final Object principal = authentication.getPrincipal();
                log.info("agt : {}", principal.toString());
                UserDetails userDetails = (UserDetails) principal;
                final String username = userDetails.getUsername();
                log.info("username : {}", username);
                userInfo.setUserId(username);
            } else {
                log.info("No authentication found");
            }
        } else {
            log.info("No security context found");
        }

        final Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("user_key")) {
                    userInfo.setTest(cookie.getValue());
                }
            }
        }
        userInfoThreadLocal.set(userInfo);

        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContest.removeUser();
    }
}
