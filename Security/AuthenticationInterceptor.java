package com.DJuanCarlosPeluqueria.DJuanCarlosPeluqueria.Security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Allow OPTIONS requests for CORS preflight
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String requestURI = request.getRequestURI();
        String method = request.getMethod();
        
        // Allow access to public endpoints
        if (isPublicEndpoint(requestURI, method)) {
            return true;
        }

        // Check for authenticated session
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("authenticatedUserId") == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"error\":\"Authentication required. Please login first.\"}");
            return false;
        }

        return true;
    }

    private boolean isPublicEndpoint(String uri, String method) {
        // Public endpoints that don't require authentication
        // Allow user registration (POST to /api/usuarios)
        if ("POST".equalsIgnoreCase(method) && uri.equals("/api/usuarios")) {
            return true;
        }
        
        return uri.equals("/api/usuarios/login") ||
               uri.startsWith("/login") ||
               uri.startsWith("/index") ||
               uri.startsWith("/ServiciosIn") ||
               uri.startsWith("/Ubicacion") ||
               uri.startsWith("/estilistas") ||
               uri.startsWith("/registro") ||
               uri.startsWith("/Static/") ||
               uri.startsWith("/css/") ||
               uri.startsWith("/javascript/") ||
               uri.startsWith("/icons/") ||
               uri.startsWith("/bostraap/");
    }
}
