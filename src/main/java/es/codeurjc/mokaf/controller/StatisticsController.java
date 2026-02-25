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

    @SuppressWarnings("unchecked")
    @GetMapping("/statistics/charts")
    public String viewCharts(Model model) {

        Map<String, Object> chartStats = statisticsService.getChartStatistics();

        // Obtener los datos
        Map<String, Object> bestProduct = (Map<String, Object>) chartStats.get("bestProduct");
        Map<String, Object> topCategory = (Map<String, Object>) chartStats.get("topCategory");
        List<Map<String, Object>> allCategories = (List<Map<String, Object>>) chartStats.get("allCategories");
        Map<String, Object> topBranch = (Map<String, Object>) chartStats.get("topBranch");
        List<Map<String, Object>> allBranches = (List<Map<String, Object>>) chartStats.get("allBranches");

        // Añadir flags explícitos en lugar de usar .exists
        model.addAttribute("hasBestProduct",
                bestProduct != null && !"No data available".equals(bestProduct.get("name")));
        model.addAttribute("hasTopCategory", topCategory != null && !"No data".equals(topCategory.get("category")));
        model.addAttribute("hasTopBranch", topBranch != null && !"No data available".equals(topBranch.get("name")));

        // Añadir los datos
        model.addAttribute("bestProduct", bestProduct);
        model.addAttribute("topCategory", topCategory);
        model.addAttribute("allCategories", allCategories);
        model.addAttribute("topBranch", topBranch);
        model.addAttribute("allBranches", allBranches);

        return "statistics";
    }
}