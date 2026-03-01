package es.codeurjc.mokaf.service;

import es.codeurjc.mokaf.model.Branch;
import es.codeurjc.mokaf.repository.BranchRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BranchService {

    private final BranchRepository branchRepository;

    public BranchService(BranchRepository branchRepository) {
        this.branchRepository = branchRepository;
    }

    public List<Branch> getAllBranches() {
        return branchRepository.findAll();
    }

    public List<Branch> getAllBranchesOrdered() {
        return branchRepository.findAllBranchesOrdered();
    }

    public Optional<Branch> getBranchById(Long id) {
        return branchRepository.findById(id);
    }

    public Optional<Branch> getFirstBranch() {
        return branchRepository.findFirstByOrderByIdAsc();
    }

    public Optional<Branch> getBranchByName(String name) {
        return branchRepository.findByName(name);
    }
}
