package com.example.pojo;

import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;
//import org.springframework.web.context.annotation.SessionScope;


@Component
@RequestScope
//@SessionScope
public class AuthHelper {
    private String sessionId;
    private boolean authed = false;
//    private boolean captchaPassed;

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

    @Override
    public String toString() {
        return "AuthHelper{" +
                "sessionId='" + sessionId + '\'' +
                ", authed=" + authed +
                '}';
    }
//    public boolean isCaptchaPassed() {
//        return captchaPassed;
//    }

//    public void setCaptchaPassed(boolean captchaPassed) {
//        this.captchaPassed = captchaPassed;
//    }
}
