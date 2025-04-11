package org.tns.exception;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.tns.response.StandardResponse;

import java.util.List;
import java.util.stream.Collectors;

@Provider
public class ValidationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    @Override
    public Response toResponse(ConstraintViolationException exception) {
        List<String> errors = exception.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        StandardResponse<List<String>> response = new StandardResponse<>(
                Response.Status.BAD_REQUEST.getStatusCode(),
                "Error",
                errors
        );

        return Response.status(Response.Status.BAD_REQUEST).entity(
                java.util.Collections.singletonMap("standardResponse", response)
        ).build();
    }
}
