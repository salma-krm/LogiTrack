package com.smartusers.logitrackapi.service.impl;
import com.smartusers.logitrackapi.entity.User;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;



@Component
public class SessionManager {

    private final Map<String, User> sessions = new ConcurrentHashMap<>();

    public String createSession(User user) {
        String sessionId = UUID.randomUUID().toString();
        sessions.put(sessionId, user);
        return sessionId;
    }

    public User getUserBySessionId(String sessionId) {
        return sessions.get(sessionId);
    }

    public void invalidateSession(String sessionId) {
        sessions.remove(sessionId);
    }

    public boolean isValidSession(String sessionId) {
        return sessions.containsKey(sessionId);
    }
}

