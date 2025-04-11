package org.tns.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "EmployeeQuar")
public class EmployeeQuar {

    @Id
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Employee Code cannot be empty")
    private String empCode;
    @NotBlank(message = "First name is required")
    @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
    private String fname;
    private String mname;
    @NotBlank(message = "Last name is required")
    private String lname;
    @NotBlank(message = "Password is required.")
//    @JsonIgnore // Hide password in API responses
    private String password;

    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference  //  Prevent infinite recursion
    @Valid
    private List<Address> addresses = new ArrayList<>();  //  Ensure list is initialized

    // Getters and Setters

    public List<Address> getAddresses() {
        return addresses;
    }

    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
        if (addresses != null) {
            for (Address address : addresses) {
                address.setEmployee(this); //  Ensure address is linked properly
            }
        }
    }

    public String getEmpCode() {
        return empCode;
    }

    public void setEmpCode(String empCode) {
        this.empCode = empCode;
    }

    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    public String getMname() {
        return mname;
    }

    public void setMname(String mname) {
        this.mname = mname;
    }

    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getPassword() {return password;}

    public void setPassword(String password) { this.password = password; }
}
