package es.codeurjc.mokaf.service;

import es.codeurjc.mokaf.repository.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class StatisticsService {

    @Autowired
    private StatisticsRepository statisticsRepository;

    // Chart 1 - Producto más vendido del mes
    public Map<String, Object> getBestSellingProductCurrentMonth() {
        List<Object[]> results = statisticsRepository.findBestSellingProductCurrentMonth();

        if (results == null || results.isEmpty()) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("name", "Sin datos");
            empty.put("category", "N/A");
            empty.put("totalUnits", 0);
            empty.put("totalAmount", 0.0);
            empty.put("totalAmountFormatted", "0.00");
            empty.put("imagePath", "/images/default-product.png");
            empty.put("exists", false);
            return empty;
        }

        Object[] row = results.get(0);
        Map<String, Object> product = new HashMap<>();
        Long productId = ((Number) row[0]).longValue(); // ID del producto
        product.put("id", productId);
        product.put("name", row[1]);
        product.put("category", row[2]);
        product.put("totalUnits", ((Number) row[3]).longValue());

        double amount = ((Number) row[4]).doubleValue();
        product.put("totalAmount", amount);
        product.put("totalAmountFormatted", String.format("%.2f", amount));

        // Usar el ID para generar la ruta de la imagen
        product.put("imagePath", "/images/" + productId);

        product.put("exists", true);

        return product;
    }

    // Chart 2 - Categoría más vendida (últimos 3 meses)
    public Map<String, Object> getTopCategoryLast3Months() {
        List<Object[]> results = statisticsRepository.findTopCategoryLast3Months();

        if (results == null || results.isEmpty()) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("category", "No data");
            empty.put("totalUnits", 0);
            empty.put("totalAmount", 0.0);
            empty.put("totalAmountFormatted", "0.00");
            empty.put("orderCount", 0);
            empty.put("exists", false);
            return empty;
        }

        // La categoría más vendida (primera de la lista)
        Object[] row = results.get(0);
        Map<String, Object> category = new HashMap<>();
        category.put("category", row[0]);
        category.put("totalUnits", ((Number) row[1]).longValue());

        double amount = ((Number) row[2]).doubleValue();
        category.put("totalAmount", amount);
        category.put("totalAmountFormatted", String.format("%.2f", amount));
        category.put("orderCount", ((Number) row[3]).longValue());
        category.put("exists", true);

        return category;
    }

    // Todas las categorías con sus ventas (últimos 3 meses)
    public List<Map<String, Object>> getAllCategoriesLast3Months() {
        List<Object[]> results = statisticsRepository.findTopCategoryLast3Months();
        List<Map<String, Object>> categories = new ArrayList<>();

        if (results == null || results.isEmpty()) {
            return categories;
        }

        // Colores para categorías
        Map<String, String> categoryColors = new HashMap<>();
        categoryColors.put("HOT", "#ff6b6b");
        categoryColors.put("COLD", "#4dabf7");
        categoryColors.put("BLENDED", "#9775fa");
        categoryColors.put("DESSERTS", "#ff8787");
        categoryColors.put("NON_COFFEE", "#69db7e");

        // Calcular total de ingresos para porcentajes
        double totalAmount = 0;
        for (Object[] row : results) {
            totalAmount += ((Number) row[2]).doubleValue();
        }

        // Procesar cada categoría
        for (Object[] row : results) {
            Map<String, Object> cat = new LinkedHashMap<>();
            cat.put("category", row[0]);
            cat.put("units", ((Number) row[1]).longValue());

            double amount = ((Number) row[2]).doubleValue();
            cat.put("amount", amount);
            cat.put("amountFormatted", String.format("%.2f", amount));

            double percentage = totalAmount > 0 ? (amount / totalAmount) * 100 : 0;
            cat.put("percentage", Math.round(percentage));

            cat.put("color", categoryColors.getOrDefault(row[0], "#adb5bd"));
            categories.add(cat);
        }

        return categories;
    }

    // Chart 3 - Sucursal con más ventas
    public Map<String, Object> getTopBranch() {
        List<Object[]> results = statisticsRepository.findTopBranch();

        if (results == null || results.isEmpty()) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("name", "No data available");
            empty.put("totalOrders", 0);
            empty.put("totalUnits", 0);
            empty.put("totalRevenue", 0.0);
            empty.put("totalRevenueFormatted", "0.00");
            empty.put("avgOrderValue", "0.00");
            empty.put("exists", false);
            return empty;
        }

        Object[] row = results.get(0);
        Map<String, Object> branch = new HashMap<>();
        branch.put("name", row[0]);
        branch.put("totalOrders", ((Number) row[1]).longValue());
        branch.put("totalUnits", ((Number) row[2]).longValue());

        double revenue = ((Number) row[3]).doubleValue();
        branch.put("totalRevenue", revenue);
        branch.put("totalRevenueFormatted", String.format("%.2f", revenue));

        double avgOrder = ((Number) row[4]).doubleValue();
        branch.put("avgOrderValue", String.format("%.2f", avgOrder));
        branch.put("exists", true);

        return branch;
    }

    // Todas las sucursales con sus ventas
    public List<Map<String, Object>> getAllBranches() {
        List<Object[]> results = statisticsRepository.findAllBranchesSales();
        List<Map<String, Object>> branches = new ArrayList<>();

        if (results == null || results.isEmpty()) {
            return branches;
        }

        // Asignar color para las sucursales
        String[] colors = { "#4263eb", "#9775fa", "#ff8787", "#69db7e", "#ffd43b", "#ff6b6b" };
        int colorIndex = 0;

        // Primero calcular el total de ingresos (manejando nulls)
        double totalRevenue = 0;
        for (Object[] row : results) {
            Number revenue = (Number) row[3];
            totalRevenue += revenue != null ? revenue.doubleValue() : 0.0;
        }

        // Procesar cada sucursal
        for (Object[] row : results) {
            Map<String, Object> branch = new LinkedHashMap<>();

            // Nombre de la sucursal
            branch.put("name", row[0] != null ? row[0] : "Sin nombre");

            // Total de pedidos (puede ser null)
            Number orders = (Number) row[1];
            branch.put("orders", orders != null ? orders.longValue() : 0L);

            // Total de unidades (puede ser null)
            Number units = (Number) row[2];
            branch.put("units", units != null ? units.longValue() : 0L);

            // Total de ingresos - ¡PUEDE SER NULL!
            Number revenue = (Number) row[3];
            double revenueValue = revenue != null ? revenue.doubleValue() : 0.0;

            branch.put("revenue", revenueValue);
            branch.put("revenueFormatted", String.format("%.2f", revenueValue));

            // Calcular porcentaje
            double percentage = totalRevenue > 0 ? (revenueValue / totalRevenue) * 100 : 0;
            branch.put("percentage", Math.round(percentage));

            // Asignar color
            branch.put("color", colors[colorIndex % colors.length]);
            colorIndex++;

            branches.add(branch);
        }

        return branches;
    }

    // Método principal que obtiene todas las estadísticas para los gráficos
    public Map<String, Object> getChartStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Producto más vendido
        stats.put("bestProduct", getBestSellingProductCurrentMonth());

        // Categorías
        stats.put("topCategory", getTopCategoryLast3Months());
        stats.put("allCategories", getAllCategoriesLast3Months());

        // Sucursales
        stats.put("topBranch", getTopBranch());
        stats.put("allBranches", getAllBranches());

        return stats;
    }

    // Obtener todas las categorías de productos (para filtros)
    public List<String> getAllProductCategories() {
        return statisticsRepository.findAllProductCategories();
    }
}