package org.tns.service;

import jakarta.validation.ValidationException;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;
import org.tns.dao.EmployeeDAO;
import org.tns.entity.EmployeeQuar;
import org.tns.entity.Address;
import org.tns.exception.ResourceNotFoundException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;


@ApplicationScoped
public class EmployeeService {
    private static final Logger LOG = Logger.getLogger(EmployeeService.class);

    @Inject
    EmployeeDAO employeeDAO;

    public List<EmployeeQuar> getAllEmployees() {
        LOG.info("Retrieving all employees from the database...");
        return employeeDAO.listAll();
    }

    public EmployeeQuar getEmployeeByEmpCode(String empCode) {
        LOG.info("Searching for employee with empCode: " + empCode);
        EmployeeQuar employee = employeeDAO.find("empCode", empCode).firstResult();
        if (employee == null) {
            LOG.error("Employee not found with empCode: " + empCode);
            throw new ResourceNotFoundException("Employee Not Found with empCode: " + empCode);
        }
        return employee;
    }


    @Transactional
    public void createEmployee(EmployeeQuar employee) {
        try {
            LOG.info("Creating new employee with empCode: " + employee.getEmpCode());

            // Check if employee already exists
            boolean exists = employeeDAO.find("empCode", employee.getEmpCode()).firstResultOptional().isPresent();
            if (exists) {
                throw new ValidationException("Employee with empCode " + employee.getEmpCode() + " already exists.");
            }

            // Hash the password using BCrypt
            if (employee.getPassword() == null || employee.getPassword().isBlank()) {
                throw new ValidationException("Password is required.");
            }
            String hashedPassword = BCrypt.hashpw(employee.getPassword(), BCrypt.gensalt());
            employee.setPassword(hashedPassword);

            // Set back-reference in addresses (to maintain relationship)
            if (employee.getAddresses() != null) {
                for (Address address : employee.getAddresses()) {
                    address.setEmployee(employee);
                }
            }

            // Persist employee (cascades addresses)
            employeeDAO.persist(employee);

            LOG.info("Employee created successfully.");
        } catch (Exception e) {
            LOG.error("Error creating employee: " + e.getMessage(), e);
            throw e;
        }
    }






    @Transactional
    public void deleteEmployeeByEmpCode(String empCode) {
        LOG.info("Deleting employee with empCode: " + empCode);
        EmployeeQuar employee = employeeDAO.find("empCode", empCode).firstResult();
        if (employee == null) {
            throw new ResourceNotFoundException("Employee not found with empCode: " + empCode);
        }
        employeeDAO.delete(employee);
        LOG.info("Employee deleted successfully.");
    }




    //Update employee
    @Transactional
    public void updateEmployee(String empCode, EmployeeQuar updatedEmployee) {
        try {
            LOG.info("Updating employee with empCode: " + empCode);

            // 1. Prevent empCode change
            if (!empCode.equals(updatedEmployee.getEmpCode())) {
                throw new ValidationException("Changing empCode is not allowed.");
            }

            // 2. Validate required fields
            if (updatedEmployee.getFname() == null || updatedEmployee.getFname().isBlank()) {
                throw new ValidationException("First name is required.");
            }

            // 3. Find existing employee
            EmployeeQuar existingEmployee = employeeDAO.find("empCode", empCode).firstResultOptional()
                    .orElseThrow(() -> new ResourceNotFoundException("Employee with empCode " + empCode + " not found."));

            // 4. Update fields
            existingEmployee.setFname(updatedEmployee.getFname());
            existingEmployee.setMname(updatedEmployee.getMname());
            existingEmployee.setLname(updatedEmployee.getLname());

            // 5. Update addresses
            existingEmployee.getAddresses().clear();
            if (updatedEmployee.getAddresses() != null) {
                for (Address address : updatedEmployee.getAddresses()) {
                    address.setEmployee(existingEmployee); // maintain relationship
                    existingEmployee.getAddresses().add(address);
                }
            }

            // 6. Persist changes
            employeeDAO.persist(existingEmployee);
            LOG.info("Employee updated successfully.");

        } catch (Exception e) {
            LOG.error("Error in updateEmployee: " + e.getMessage(), e);
            throw e; // Let Resource layer handle and return proper response
        }
    }

    public boolean validateCredentials(String empCode, String password) {
        EmployeeQuar emp = employeeDAO.findByEmpCode(empCode);
        if (emp != null) {
            return BCrypt.checkpw(password, emp.getPassword());
        }
        return false;
    }





}
