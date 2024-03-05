package com.example.interceptors;

import com.example.pojo.AuthHelper;
import com.example.pojo.Quota;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.args.ExpiryOption;
import redis.clients.jedis.params.SetParams;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This interceptor initializes the requestScope-ed Quota object and AuthHelper object
 */
@Component
public class AllRouteCheckInterceptor implements HandlerInterceptor {
    @Autowired
    Jedis jedis;
    @Autowired
    private Quota quotaObject;
    @Autowired
    private AuthHelper authHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String sid = request.getSession().getId();

        authHelper.setSessionId(sid);

        // I can't use request.getRemoteAddr() because I am using nginx as reverse proxy from localhost,
        // and this method always returns localhost ip addresses
//         String ip = request.getRemoteAddr();

        // configured nginx to add this header that contains the real IP
        String ip = request.getHeader("X-Real-IP");

        quotaObject.setIp(ip);
        String quotaKey = "ip-" + ip;
        Integer quotaValue = null;
        String quotas = jedis.get(quotaKey);
        // first check if logged in as an admin; if logged in, no need to check the quotas
        String authStatus = jedis.get("sid-" + sid);
        if ("yes".equals(authStatus)) {
            quotaObject.setRemaining(50);
            authHelper.setAuthed(true);
            return true;
        }

        // not authed
        authHelper.setAuthed(false);

        // if not logged in, check their eligibility to query
        if (quotas == null) {
            quotaValue = 50;
            // never visited or last limitation expired
            jedis.set(quotaKey, Integer.toString(quotaValue));
            jedis.expire(quotaKey, 86400, ExpiryOption.NX);  // the limitation expires after 1 day
        } else {
            int q = Integer.parseInt(quotas);
            if (q > 0) {
                quotaValue = q-1;
                jedis.set(quotaKey, String.valueOf(quotaValue), SetParams.setParams().keepttl());
            } else {
                // limitation reached
                response.sendRedirect("/limit_reached");
                return false;
            }
        }
        quotaObject.setRemaining(quotaValue);
        System.out.println("quotaObject = " + quotaObject);
        return true;
//        return HandlerInterceptor.super.preHandle(request, response, handler);
    }
}
