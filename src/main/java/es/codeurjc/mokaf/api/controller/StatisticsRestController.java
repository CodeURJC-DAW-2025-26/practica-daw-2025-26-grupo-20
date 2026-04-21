package es.codeurjc.mokaf.api.controller;

import java.util.Map;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import es.codeurjc.mokaf.api.dto.StatsDTO.*;
import es.codeurjc.mokaf.api.exception.ResourceNotFoundException;
import es.codeurjc.mokaf.api.mapper.StatisticsMapper;
import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.service.StatisticsService;
import es.codeurjc.mokaf.service.UserService;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsRestController {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private StatisticsMapper statisticsMapper;

    @Autowired
    private UserService userService;

    // ============ Helper To verify that is admin============


    private User resolveAdmin(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, 
                "Debes iniciar sesión para acceder a las estadísticas");
        }

        Object principal = authentication.getPrincipal();
        User user;

        if (principal instanceof User u) {
            user = u;
        } else if (principal instanceof String email) {
            user = userService.findByEmail(email)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));
        } else {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, 
                "No se pudo identificar al usuario");
        }

        if (user.getRole() != User.Role.ADMIN) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, 
                "Acceso denegado. Solo administradores pueden ver estadísticas");
        }

        return user;
    }

    // ============ Endpoints ============

    @GetMapping("/dashboard")
    public StatisticsDTO getDashboardStatistics(Authentication authentication) {
        resolveAdmin(authentication); 

        Map<String, Object> stats = statisticsService.getChartStatistics();

        @SuppressWarnings("unchecked")
        Map<String, Object> bestProductMap = (Map<String, Object>) stats.get("bestProduct");
        @SuppressWarnings("unchecked")
        Map<String, Object> topRatedProductMap = (Map<String, Object>) stats.get("topRatedProduct");
        @SuppressWarnings("unchecked")
        Map<String, Object> topCategoryMap = (Map<String, Object>) stats.get("topCategory");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> allCategoriesMaps = (List<Map<String, Object>>) stats.get("allCategories");
        @SuppressWarnings("unchecked")
        Map<String, Object> topBranchMap = (Map<String, Object>) stats.get("topBranch");
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> allBranchesMaps = (List<Map<String, Object>>) stats.get("allBranches");

        
        ProductStatDTO bestProduct = processProductWithReviews(bestProductMap);
        ProductStatDTO topRatedProduct = processProductWithReviews(topRatedProductMap);

        StatisticsDTO statisticsDTO = new StatisticsDTO(
                bestProduct,
                topRatedProduct,
                statisticsMapper.toCategoryStatDTO(topCategoryMap),
                statisticsMapper.toCategoryStatDTOs(allCategoriesMaps),
                statisticsMapper.toBranchStatDTO(topBranchMap),
                statisticsMapper.toBranchStatDTOs(allBranchesMaps));

        return statisticsDTO;
    }

   
    @GetMapping("/top-rated-product")
    public ProductStatDTO getTopRatedProduct(Authentication authentication) {
        resolveAdmin(authentication);

        Map<String, Object> topRatedProduct = statisticsService.getTopRatedProductLastMonth();
        if (topRatedProduct == null || !Boolean.TRUE.equals(topRatedProduct.get("exists"))) {
            throw new ResourceNotFoundException("No hay datos de producto mejor valorado");
        }
        return processProductWithReviews(topRatedProduct);
    }

    @GetMapping("/top-category")
    public CategoryStatDTO getTopCategory(Authentication authentication) {
        resolveAdmin(authentication); 

        Map<String, Object> topCategory = statisticsService.getTopCategoryLast3Months();
        if (topCategory == null || !Boolean.TRUE.equals(topCategory.get("exists"))) {
            throw new ResourceNotFoundException("No hay datos de categoría más vendida");
        }
        return statisticsMapper.toCategoryStatDTO(topCategory);
    }

    @GetMapping("/categories")
    public List<CategoryStatDTO> getAllCategories(Authentication authentication) {
        resolveAdmin(authentication); 

        List<Map<String, Object>> categories = statisticsService.getAllCategoriesLast3Months();
        return statisticsMapper.toCategoryStatDTOs(categories);
    }

    @GetMapping("/top-branch")
    public BranchStatDTO getTopBranch(Authentication authentication) {
        resolveAdmin(authentication); 

        Map<String, Object> topBranch = statisticsService.getTopBranch();
        if (topBranch == null || !Boolean.TRUE.equals(topBranch.get("exists"))) {
            throw new ResourceNotFoundException("No hay datos de sucursal más vendida");
        }
        return statisticsMapper.toBranchStatDTO(topBranch);
    }

    @GetMapping("/branches")
    public List<BranchStatDTO> getAllBranches(Authentication authentication) {
        resolveAdmin(authentication);

        List<Map<String, Object>> branches = statisticsService.getAllBranches();
        return statisticsMapper.toBranchStatDTOs(branches);
    }

    @GetMapping("/categories/list")
    public List<String> getAllProductCategories(Authentication authentication) {
        resolveAdmin(authentication);

        return statisticsService.getAllProductCategories();
    }

    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        
        return Map.of(
                "status", "OK",
                "service", "Statistics REST API",
                "timestamp", java.time.LocalDateTime.now().toString());
    }

    
    private ProductStatDTO processProductWithReviews(Map<String, Object> productMap) {
        if (productMap == null)
            return null;

        
        return statisticsMapper.toProductStatDTO(productMap);
    }
}