package com.example.pojo;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@Component
@RequestScope  // equivalent to Scope(value=xxxx.REQUEST, proxyMode=xxx.TARGET_CLASS)
public class Quota {
    private Integer remaining;
    private String ip;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public Integer getRemaining() {
        return remaining;
    }

    @Override
    public String toString() {
        return "Quota{" +
                "remaining=" + remaining +
                ", ip='" + ip + '\'' +
                '}';
    }

    public void setRemaining(Integer remaining) {
        this.remaining = remaining;
    }
}
