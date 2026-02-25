package es.codeurjc.mokaf.controller;

import es.codeurjc.mokaf.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @GetMapping("/statistics/charts")
    @SuppressWarnings("unchecked")
    public String viewCharts(Model model) {

        Map<String, Object> chartStats = statisticsService.getChartStatistics();

        // Get data
        Map<String, Object> bestProduct = (Map<String, Object>) chartStats.get("bestProduct");
        Map<String, Object> topRatedProduct = (Map<String, Object>) chartStats.get("topRatedProduct"); // NEW
        Map<String, Object> topCategory = (Map<String, Object>) chartStats.get("topCategory");
        List<Map<String, Object>> allCategories = (List<Map<String, Object>>) chartStats.get("allCategories");
        Map<String, Object> topBranch = (Map<String, Object>) chartStats.get("topBranch");
        List<Map<String, Object>> allBranches = (List<Map<String, Object>>) chartStats.get("allBranches");

        // Add explicit flags
        model.addAttribute("hasBestProduct",
                bestProduct != null && bestProduct.containsKey("exists") && (boolean) bestProduct.get("exists"));
        model.addAttribute("hasTopRatedProduct", topRatedProduct != null && topRatedProduct.containsKey("exists")
                && (boolean) topRatedProduct.get("exists")); // NEW
        model.addAttribute("hasTopCategory",
                topCategory != null && topCategory.containsKey("exists") && (boolean) topCategory.get("exists"));
        model.addAttribute("hasTopBranch",
                topBranch != null && topBranch.containsKey("exists") && (boolean) topBranch.get("exists"));

        // Add data
        model.addAttribute("bestProduct", bestProduct);
        model.addAttribute("topRatedProduct", topRatedProduct); // NEW
        model.addAttribute("topCategory", topCategory);
        model.addAttribute("allCategories", allCategories);
        model.addAttribute("topBranch", topBranch);
        model.addAttribute("allBranches", allBranches);

        return "statistics";
    }
}