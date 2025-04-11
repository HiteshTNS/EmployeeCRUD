package org.tns.resoure;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import jakarta.ws.rs.Path;
import org.hibernate.cfg.Environment;

@Path("/")
public class Resource {
    @ConfigProperty(name="Environment",defaultValue = "Error")
    String Environment;
    @GET
    @Path("Environment")
    public Response getEnvironmentName(){
        return Response.ok(Environment).build();
    }

}
