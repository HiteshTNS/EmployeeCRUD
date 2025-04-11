package org.tns.entity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "Address")
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-generate primary key
    private Long id;
    @NotBlank(message = "Address Line 1 cannot be empty")
    private String add1;
    private String add2;
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be exactly 10 digits")
    private String phoneNo;
    @NotBlank(message = "Zip Code is required")
    @Size(min = 5, max = 10, message = "Zip Code must be between 5 and 10 characters")
    private String zipCode;
    @NotBlank(message = "Country cannot be empty")
    private String country;

    @Column(name = "emp_code", nullable = false)  // Store empCode explicitly
    private String empCode;

    @ManyToOne
    @JoinColumn(name = "emp_code", referencedColumnName = "empCode", insertable = false, updatable = false)
    @JsonBackReference  // Prevent infinite recursion
    private EmployeeQuar employee;  //  Many Addresses belong to One Employee

    // Automatically set empCode when setting Employee
    public void setEmployee(EmployeeQuar employee) {
        this.employee = employee;
        if (employee != null) {
            this.empCode = employee.getEmpCode();  //  Ensure empCode is stored
        }
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EmployeeQuar getEmployee() {
        return employee;
    }

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public void setPhoneNo(String phoneNo) {
        this.phoneNo = phoneNo;
    }

    public String getAdd2() {
        return add2;
    }

    public void setAdd2(String add2) {
        this.add2 = add2;
    }

    public String getAdd1() {
        return add1;
    }

    public void setAdd1(String add1) {
        this.add1 = add1;
    }
}
