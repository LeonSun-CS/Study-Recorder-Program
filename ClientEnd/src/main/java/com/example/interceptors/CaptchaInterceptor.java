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
 * This interceptor checks the captcha status of current browsing session; during first
 * visit, the user is redirected to the captcha check page, and after passing the captcha,
 * a redis record is set in the format of "captcha-<sessionId>" with a value of "yes"
 */
@Component
public class CaptchaInterceptor implements HandlerInterceptor {
    @Autowired
    JedisPool jedisPool;
    @Autowired
    AuthHelper authHelper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String sid = request.getSession().getId();
        // TODO: DEBUG *******************************
        System.out.println("request.getRequestURI() = " + request.getRequestURI());

        authHelper.setSessionId(sid);

        try (Jedis jedis = jedisPool.getResource()) {

            String status = jedis.get("captcha-" + sid);
            if (status == null) {
                String returnUrl = request.getRequestURI();

                // TODO: DEBUG *******************************
                System.out.println("/captcha" + (returnUrl != null && !returnUrl.isEmpty() ? "?returnUrl=" + returnUrl : ""));
                // TODO: DEBUG *******************************

                response.sendRedirect("/captcha" + (returnUrl != null && !returnUrl.isEmpty() ? "?returnUrl=" + returnUrl : ""));
                return false;
            }

            // not necessary because we never use the isCaptchaPassed() method,
            // so we can just proceed to the next step
            // authHelper.setCaptchaPassed(true);

            return true;
//        return HandlerInterceptor.super.preHandle(request, response, handler);
        }
    }
}
