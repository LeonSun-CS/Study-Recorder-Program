package com.example.interceptors;

import com.example.pojo.AuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This interceptor authenticates accesses to the admin tools.
 */
@Component
public class AdminAuthWallInterceptor implements HandlerInterceptor {
    @Autowired
    JedisPool jedisPool;
    @Autowired
    AuthHelper authHelper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("/limit_reached".equals(request.getRequestURI())) {
            // this URI is allowed to be accessed without authentication, and it doesn't
            // go through the quota check, where authHelper's sessionId is set
            // so, we need to set it explicitly here
            authHelper.setSessionId(request.getSession().getId());
            return true;
        }

        try (Jedis jedis = jedisPool.getResource()) {

            String status = jedis.get("sid-" + authHelper.getSessionId());
            if (status == null) {
                authHelper.setAuthed(false);
                String returnUrl = request.getRequestURI();
                response.sendRedirect("/auth" + (returnUrl != null && !returnUrl.isEmpty() ? "?returnUrl=" + returnUrl : ""));
                return false;
            }

            authHelper.setAuthed(true);
            return true;
//        return HandlerInterceptor.super.preHandle(request, response, handler);
        }
    }
}
