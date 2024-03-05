package com.example.interceptors;

import com.example.pojo.AuthHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import redis.clients.jedis.Jedis;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This interceptor authenticates accesses to the admin tools.
 */
@Component
public class AdminAuthWallInterceptor implements HandlerInterceptor {
    @Autowired
    Jedis jedis;
    @Autowired
    AuthHelper authHelper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String sid = request.getSession().getId();
        authHelper.setSessionId(sid);
        String status = jedis.get("sid-" + sid);
        if (status == null) {
            authHelper.setAuthed(false);
            response.sendRedirect("/auth");
            return false;
        }

        authHelper.setAuthed(true);
        return true;
//        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
