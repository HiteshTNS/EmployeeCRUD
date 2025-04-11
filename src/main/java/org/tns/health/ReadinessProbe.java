package org.tns.health;

import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.tns.dao.EmployeeDAO;

@Readiness
@ApplicationScoped
public class ReadinessProbe implements HealthCheck {

    @Inject
    EmployeeDAO employeeDAO;  // Example for DB check

    @Override
    public HealthCheckResponse call() {
        boolean isDatabaseUp = employeeDAO.listAll() != null;

        return isDatabaseUp
                ? HealthCheckResponse.up("Application is ready!")
                : HealthCheckResponse.down("Database is not available");
    }
}
