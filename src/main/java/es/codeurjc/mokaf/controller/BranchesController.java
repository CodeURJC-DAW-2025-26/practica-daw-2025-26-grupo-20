package es.codeurjc.mokaf.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import es.codeurjc.mokaf.model.Branch;
import es.codeurjc.mokaf.repository.BranchRepository;

@Controller
public class BranchesController {

    @Autowired
    private BranchRepository branchRepository;
    
     @GetMapping("/branches")
    public String branches ( Model model) {

        List<Branch> branches = branchRepository.findAll();
        model.addAttribute("branches", branches);
       
        return "branches";
    }
}
