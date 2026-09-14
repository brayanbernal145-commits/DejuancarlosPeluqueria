package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Model;

import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthenticationService {
    
    // In-memory session store (for production, use Redis or database)
    private final Map<String, Long> sessionStore = new ConcurrentHashMap<>();
    private final Map<String, Integer> sessionRoles = new ConcurrentHashMap<>();
    
    /**
     * Create a session for authenticated user
     * @param userId The user ID
     * @param roleId The role ID
     * @return Session token
     */
    public String createSession(Long userId, Integer roleId) {
        String token = UUID.randomUUID().toString();
        sessionStore.put(token, userId);
        sessionRoles.put(token, roleId);
        return token;
    }
    
    /**
     * Validate session token and return user ID
     * @param token Session token
     * @return User ID or null if invalid
     */
    public Long validateSession(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        return sessionStore.get(token);
    }
    
    /**
     * Get role ID for session
     * @param token Session token
     * @return Role ID or null if invalid
     */
    public Integer getSessionRole(String token) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        return sessionRoles.get(token);
    }
    
    /**
     * Check if user is administrator (role ID 1)
     * @param token Session token
     * @return true if admin, false otherwise
     */
    public boolean isAdmin(String token) {
        Integer roleId = getSessionRole(token);
        return roleId != null && roleId == 1;
    }
    
    /**
     * Invalidate session
     * @param token Session token
     */
    public void invalidateSession(String token) {
        if (token != null) {
            sessionStore.remove(token);
            sessionRoles.remove(token);
        }
    }
}
