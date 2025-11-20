package com.smartusers.logitrackapi.service.impl;

import com.smartusers.logitrackapi.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SessionManagerTest {

    private SessionManager sessionManager;
    private User user;

    @BeforeEach
    void setUp() {
        sessionManager = new SessionManager();
        user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");
        user.setPassword("password");
    }

    @Test
    void testCreateSession() {
        String sessionId = sessionManager.createSession(user);
        assertNotNull(sessionId, "Session ID should not be null");
        assertTrue(sessionManager.isValidSession(sessionId), "Session should be valid after creation");
        assertEquals(user, sessionManager.getUserBySessionId(sessionId), "User should match the session");
    }

    @Test
    void testGetUserBySessionId_Invalid() {
        assertNull(sessionManager.getUserBySessionId("invalid-session-id"), "Should return null for invalid session");
    }

    @Test
    void testInvalidateSession() {
        String sessionId = sessionManager.createSession(user);
        assertTrue(sessionManager.isValidSession(sessionId), "Session should be valid before invalidation");

        sessionManager.invalidateSession(sessionId);
        assertFalse(sessionManager.isValidSession(sessionId), "Session should be invalid after invalidation");
        assertNull(sessionManager.getUserBySessionId(sessionId), "User should be null after session is invalidated");
    }

    @Test
    void testIsValidSession() {
        String sessionId = sessionManager.createSession(user);
        assertTrue(sessionManager.isValidSession(sessionId), "Session should be valid");
        sessionManager.invalidateSession(sessionId);
        assertFalse(sessionManager.isValidSession(sessionId), "Session should not be valid after invalidation");
    }
}
