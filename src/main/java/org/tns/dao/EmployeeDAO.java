package org.tns.dao;

import org.tns.entity.EmployeeQuar;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EmployeeDAO implements PanacheRepository<EmployeeQuar> {
    // PanacheRepository provides built-in methods like persist(), findById(), listAll()
    public EmployeeQuar findByEmpCode(String empCode) {
        return find("empCode", empCode).firstResult();
    }
}
