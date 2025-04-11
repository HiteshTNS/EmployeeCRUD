package org.tns.resource;

import jakarta.validation.Valid;
import jakarta.validation.ValidationException;
import org.jboss.logging.Logger;
import org.tns.entity.EmployeeQuar;
import org.tns.exception.ResourceNotFoundException;
import org.tns.response.StandardResponse;
import org.tns.service.EmployeeService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.tns.service.JwtService;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Path("/employees")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmployeeResource {
    private static final Logger LOG = Logger.getLogger(EmployeeResource.class);

    @Inject
    EmployeeService employeeService;
    @Inject
    JwtService jwtService;
    @GET
    public Response getAllEmployees() {
        try {
            LOG.info("Fetching all employees...");
            List<EmployeeQuar> employees = employeeService.getAllEmployees();

            return buildResponse(Response.Status.OK.getStatusCode(), "Employees fetched successfully", employees);
        } catch (ResourceNotFoundException error) {
            throw error;
        } catch (Exception error) {
            LOG.error("Failed to fetch employees: " + error.getMessage(), error);
            return buildResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Failed to fetch employees: " + error.getMessage(), null);
        }
    }




    @GET
    @Path("/empcode/{empCode}")
    public Response getEmployeeByEmpCode(@PathParam("empCode") String empCode) {
        try {
            LOG.info("Fetching employee with empCode: " + empCode);
            EmployeeQuar employee = employeeService.getEmployeeByEmpCode(empCode);
            return buildResponse(Response.Status.OK.getStatusCode(), "Employee fetched successfully", employee);
        } catch (ResourceNotFoundException error) {
            LOG.warn("Employee not found: " + empCode);
            return buildResponse(Response.Status.NOT_FOUND.getStatusCode(), error.getMessage(), null);
        } catch (Exception error) {
            LOG.error("Failed to fetch employee: " + error.getMessage(), error);
            return buildResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Failed to fetch employee: " + error.getMessage(), null);
        }
    }



    @POST
    public Response createEmployee(@Valid EmployeeQuar employee) {
        try {
            LOG.info("Creating a new employee: " + employee.getEmpCode());
            employeeService.createEmployee(employee);
            return buildResponse(Response.Status.CREATED.getStatusCode(), "Employee created successfully", employee);
        } catch (ValidationException error) {
            LOG.warn("Validation failed: " + error.getMessage());
            return buildResponse(Response.Status.BAD_REQUEST.getStatusCode(), error.getMessage(), null);
        } catch (Exception error) {
            LOG.error("Failed to create employee: " + error.getMessage(), error);
            return buildResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Failed to create employee: " + error.getMessage(), null);
        }
    }




    @PUT
    @Path("/empcode/{empCode}")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateEmployee(@PathParam("empCode") String empCode, @Valid EmployeeQuar employee) {
        try {
            LOG.info("Updating employee with empCode: " + empCode);
            employeeService.updateEmployee(empCode, employee);

            String successMessage = "Employee with empCode " + empCode + " updated successfully.";
            return buildResponse(Response.Status.OK.getStatusCode(), successMessage, employee);

        } catch (ValidationException | ResourceNotFoundException error) {
            LOG.warn("Validation or Not Found error during update: " + error.getMessage());
            return buildResponse(Response.Status.BAD_REQUEST.getStatusCode(), error.getMessage(), null);

        } catch (Exception error) {
            LOG.error("Error while updating employee: " + error.getMessage(), error);
            return buildResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                    "Internal server error occurred during update.", null);
        }
    }



    @DELETE
    @Path("/empcode/{empCode}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteEmployeeByEmpCode(@PathParam("empCode") String empCode) {
        try {
            LOG.info("Deleting employee with empCode: " + empCode);
            employeeService.deleteEmployeeByEmpCode(empCode);

            String successMessage = "Employee with empCode " + empCode + " deleted successfully.";
            return buildResponse(Response.Status.OK.getStatusCode(), successMessage, null);

        } catch (ResourceNotFoundException error) {
            LOG.warn("Employee not found for deletion: " + empCode);
            return buildResponse(Response.Status.NOT_FOUND.getStatusCode(), error.getMessage(), null);

        } catch (Exception error) {
            LOG.error("Failed to delete employee: " + error.getMessage(), error);
            return buildResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                    "Internal error occurred while deleting.", null);
        }
    }


    @GET
    @Path("/secure")
    public Response securedEndpoint(@HeaderParam("Authorization") String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Missing or invalid Authorization header").build();
        }

        String token = authHeader.substring("Bearer ".length());
        try {
            String empCode = jwtService.validateToken(token);
            return Response.ok("Welcome, " + empCode).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Invalid or expired token").build();
        }
    }

    private <T> Response buildResponse(int code, String message, T data) {
        StandardResponse<T> standardResponse = new StandardResponse<>();
        standardResponse.setResponseCode(code);
        standardResponse.setResponseDiscription(message);
        standardResponse.setData(data);

        Map<String, Object> wrapper = new HashMap<>();
        wrapper.put("standardResponse", standardResponse);

        return Response.status(code).entity(wrapper).build();
    }



}
