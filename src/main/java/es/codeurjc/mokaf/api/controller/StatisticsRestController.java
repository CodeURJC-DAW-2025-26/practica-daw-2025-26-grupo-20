package es.codeurjc.mokaf.api.controller;

import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import es.codeurjc.mokaf.api.dto.ReviewDTO;
import es.codeurjc.mokaf.api.dto.StatsDTO.*;
import es.codeurjc.mokaf.api.exception.ResourceNotFoundException;
import es.codeurjc.mokaf.api.mapper.StatisticsMapper;
import es.codeurjc.mokaf.api.mapper.ReviewMapper;
import es.codeurjc.mokaf.service.StatisticsService;

@RestController
@RequestMapping("/api/v1/statistics")
public class StatisticsRestController {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private StatisticsMapper statisticsMapper;

    @Autowired
    private ReviewMapper reviewMapper;

    @GetMapping("/dashboard")
    public StatisticsDTO getDashboardStatistics() {
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

        // Procesar productos con sus reviews
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

    @GetMapping("/best-product")
    public ProductStatDTO getBestSellingProduct() {
        Map<String, Object> bestProduct = statisticsService.getBestSellingProductCurrentMonth();
        if (bestProduct == null || !Boolean.TRUE.equals(bestProduct.get("exists"))) {
            throw new ResourceNotFoundException("No hay datos de producto más vendido");
        }
        return processProductWithReviews(bestProduct);
    }

    @GetMapping("/top-rated-product")
    public ProductStatDTO getTopRatedProduct() {
        Map<String, Object> topRatedProduct = statisticsService.getTopRatedProductLastMonth();
        if (topRatedProduct == null || !Boolean.TRUE.equals(topRatedProduct.get("exists"))) {
            throw new ResourceNotFoundException("No hay datos de producto mejor valorado");
        }
        return processProductWithReviews(topRatedProduct);
    }

    @GetMapping("/top-category")
    public CategoryStatDTO getTopCategory() {
        Map<String, Object> topCategory = statisticsService.getTopCategoryLast3Months();
        if (topCategory == null || !Boolean.TRUE.equals(topCategory.get("exists"))) {
            throw new ResourceNotFoundException("No hay datos de categoría más vendida");
        }
        return statisticsMapper.toCategoryStatDTO(topCategory);
    }

    @GetMapping("/categories")
    public List<CategoryStatDTO> getAllCategories() {
        List<Map<String, Object>> categories = statisticsService.getAllCategoriesLast3Months();
        return statisticsMapper.toCategoryStatDTOs(categories);
    }

    @GetMapping("/top-branch")
    public BranchStatDTO getTopBranch() {
        Map<String, Object> topBranch = statisticsService.getTopBranch();
        if (topBranch == null || !Boolean.TRUE.equals(topBranch.get("exists"))) {
            throw new ResourceNotFoundException("No hay datos de sucursal más vendida");
        }
        return statisticsMapper.toBranchStatDTO(topBranch);
    }

    @GetMapping("/branches")
    public List<BranchStatDTO> getAllBranches() {
        List<Map<String, Object>> branches = statisticsService.getAllBranches();
        return statisticsMapper.toBranchStatDTOs(branches);
    }

    @GetMapping("/categories/list")
    public List<String> getAllProductCategories() {
        return statisticsService.getAllProductCategories();
    }

    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        return Map.of(
                "status", "OK",
                "service", "Statistics REST API",
                "timestamp", java.time.LocalDateTime.now().toString());
    }

    // Método auxiliar para procesar productos con sus reviews
    private ProductStatDTO processProductWithReviews(Map<String, Object> productMap) {
        if (productMap == null)
            return null;

        // El mapper ya no necesita manejar reviews
        return statisticsMapper.toProductStatDTO(productMap);
    }
}