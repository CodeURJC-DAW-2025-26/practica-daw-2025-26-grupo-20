package es.codeurjc.mokaf.service;

import es.codeurjc.mokaf.model.Employee;
import es.codeurjc.mokaf.repository.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository employeeRepository;

    public EmployeeService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department);
    }

    public java.util.Optional<Employee> getEmployeeById(String id) {
        return employeeRepository.findById(id);
    }

    public void save(Employee employee) {
        employeeRepository.save(employee);
    }
}
