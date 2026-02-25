package es.codeurjc.mokaf.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import es.codeurjc.mokaf.repository.EmployeeRepository;

@Controller
public class AboutUsController {

    private final EmployeeRepository employeeRepository;

    public AboutUsController(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @GetMapping("/nosotros")
    public String nosotros(Model model) {
        // Fetch team from DB
        model.addAttribute("team", employeeRepository.findByDepartment("Atencion al cliente"));
        return "nosotros";
    }

}
