// Employee.java
package es.codeurjc.mokaf.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @Column(length = 50)
    private String id; // "EMP-001", etc.

    @Column(name = "first_name", nullable = false, length = 120)
    private String firstName;

    @Column(name = "last_name", nullable = false, length = 120)
    private String lastName;

    @Column(nullable = false, length = 100)
    private String position;

    @Column(name = "department", length = 100)
    private String department;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal salary;

    @CreationTimestamp
    @Column(name = "hire_date", updatable = false)
    private LocalDateTime hireDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id")
    private Branch branch;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "email", length = 150)
    private String email;

    @Column(name = "profile_image_url", length = 255)
    private String profileImageUrl;

    public Employee() {}

    public Employee(String id, String firstName, String lastName, String position, String department, BigDecimal salary,
            Branch branch, String description, String email, String profileImageUrl) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.position = position;
        this.department = department;
        this.salary = salary;
        this.branch = branch;
        this.description = description;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
    }

    // getters/setters (igual que los tuyos)
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPosition() { return position; }
    public void setPosition(String position) { this.position = position; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public BigDecimal getSalary() { return salary; }
    public void setSalary(BigDecimal salary) { this.salary = salary; }

    public LocalDateTime getHireDate() { return hireDate; }
    public void setHireDate(LocalDateTime hireDate) { this.hireDate = hireDate; }

    public Branch getBranch() { return branch; }
    public void setBranch(Branch branch) { this.branch = branch; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getProfileImageUrl() { return profileImageUrl; }
    public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }
}