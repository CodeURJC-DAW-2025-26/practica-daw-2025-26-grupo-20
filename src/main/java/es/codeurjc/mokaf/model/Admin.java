package es.codeurjc.mokaf.model;

import javax.persistence.Entity;

@Entity
public class Admin extends User {

    private String employeeId;

    public Admin() {
    }

    public Admin(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }
}