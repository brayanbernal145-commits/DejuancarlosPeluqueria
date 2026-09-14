package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class SessionHelper {

    public static Long getAuthenticatedUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (Long) session.getAttribute("authenticatedUserId");
        }
        return null;
    }

    public static Integer getAuthenticatedUserRole(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (Integer) session.getAttribute("authenticatedUserRole");
        }
        return null;
    }

    public static String getAuthenticatedUserCargo(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (String) session.getAttribute("authenticatedUserCargo");
        }
        return null;
    }

    public static boolean isAdmin(HttpServletRequest request) {
        Integer roleId = getAuthenticatedUserRole(request);
        // Assuming role ID 1 is Admin
        return roleId != null && roleId == 1;
    }

    public static boolean isEmployee(HttpServletRequest request) {
        Integer roleId = getAuthenticatedUserRole(request);
        // Assuming role ID 2 is Employee
        return roleId != null && roleId == 2;
    }

    public static void setAuthenticatedUser(HttpSession session, Long userId, Integer roleId, String cargo) {
        session.setAttribute("authenticatedUserId", userId);
        session.setAttribute("authenticatedUserRole", roleId);
        session.setAttribute("authenticatedUserCargo", cargo);
    }

    public static void clearAuthentication(HttpSession session) {
        session.invalidate();
    }
}
