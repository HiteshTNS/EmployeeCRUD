package org.tns.exception;

import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.tns.response.StandardResponse;

import java.util.HashMap;
import java.util.Map;

@Provider
public class ResourceNotFoundExceptionMapper implements ExceptionMapper<ResourceNotFoundException> {

    @Override
    public Response toResponse(ResourceNotFoundException exception) {
        StandardResponse<Object> standardResponse = new StandardResponse<>();
        standardResponse.setResponseCode(Response.Status.NOT_FOUND.getStatusCode());
        standardResponse.setResponseDiscription(exception.getMessage());
        standardResponse.setData(null);

        Map<String, Object> responseWrapper = new HashMap<>();
        responseWrapper.put("standardResponse", standardResponse);

        return Response.status(Response.Status.NOT_FOUND).entity(responseWrapper).build();
    }
}
