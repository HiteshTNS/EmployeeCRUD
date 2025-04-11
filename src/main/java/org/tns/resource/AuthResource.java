package org.tns.resource;

import org.tns.dto.AuthRequest;
import org.tns.dto.AuthResponse;
import org.tns.service.EmployeeService;
import org.tns.service.JwtService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthResource {

    @Inject
    EmployeeService employeeService;

    @Inject
    JwtService jwtService;

    @POST
    @Path("/login")
    public Response login(AuthRequest request) {
        boolean isValid = employeeService.validateCredentials(request.empCode, request.password);
        if (isValid) {
            String token = jwtService.generateToken(request.empCode);
            return Response.ok(new AuthResponse(token)).build();
        } else {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid credentials").build();
        }
    }
}
