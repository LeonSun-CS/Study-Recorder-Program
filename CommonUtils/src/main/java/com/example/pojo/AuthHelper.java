package com.example.pojo;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;


@Component
@RequestScope
public class AuthHelper {
    public String sessionId;
    public boolean authed;

    @Override
    public String toString() {
        return "AuthHelper{" +
                "sessionId='" + sessionId + '\'' +
                ", authed=" + authed +
                '}';
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public boolean isAuthed() {
        return authed;
    }

    public void setAuthed(boolean authed) {
        this.authed = authed;
    }
}
