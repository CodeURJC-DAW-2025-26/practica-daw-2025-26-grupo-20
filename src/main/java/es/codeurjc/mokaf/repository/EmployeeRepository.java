package es.codeurjc.mokaf.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import es.codeurjc.mokaf.model.Employee;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, String> {
    List<Employee> findByDepartment(String department);
}
