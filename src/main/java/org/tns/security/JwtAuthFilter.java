package org.tns.security;

import jakarta.annotation.Priority;
import jakarta.inject.Inject;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.PreMatching;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import org.tns.service.JwtService;

@Provider
@Priority(Priorities.AUTHENTICATION)
@PreMatching
public class JwtAuthFilter implements ContainerRequestFilter {

    @Inject
    JwtService jwtService;

    @Override
    public void filter(ContainerRequestContext requestContext) {
        String path = requestContext.getUriInfo().getPath();
        System.out.println("Intercepted path: " + path);

        // Allow unauthenticated access to /auth/login
        if (path.contains("auth/login") || path.startsWith("q/") || path.contains("swagger")) {
            return; // Allow unauthenticated access
        }

        String authHeader = requestContext.getHeaderString("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Missing or invalid Authorization header").build());
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        try {
            jwtService.validateToken(token);
            // Optionally,can set user info into context here
        } catch (Exception e) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
                    .entity("Invalid or expired token").build());
        }
    }
}
