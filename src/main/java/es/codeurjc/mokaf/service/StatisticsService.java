package es.codeurjc.mokaf.service;

import es.codeurjc.mokaf.model.Branch;
import es.codeurjc.mokaf.model.Product;
import es.codeurjc.mokaf.model.Review;
import es.codeurjc.mokaf.repository.BranchRepository; // IMPORTANT: Add this import
import es.codeurjc.mokaf.repository.StatisticsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import es.codeurjc.mokaf.repository.ReviewRepository;
import es.codeurjc.mokaf.repository.ProductRepository;

import java.util.*;

@Service
public class StatisticsService {

    @Autowired
    private StatisticsRepository statisticsRepository;
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Get the best selling product of the current month (by units sold)
     * @return Map with product details including image path
     */
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
        Long productId = ((Number) row[0]).longValue(); // Product ID
        product.put("id", productId);
        product.put("name", row[1]);
        product.put("category", row[2]);
        product.put("totalUnits", ((Number) row[3]).longValue());

        double amount = ((Number) row[4]).doubleValue();
        product.put("totalAmount", amount);
        product.put("totalAmountFormatted", String.format("%.2f", amount));

        // Use ID to generate image path
        product.put("imagePath", "/images/" + productId);

        product.put("exists", true);

        return product;
    }

    /**
     * Get the top rated product from the last month (by average stars, minimum 3 reviews)
     * @return Map with product details including recent reviews
     */
    public Map<String, Object> getTopRatedProductLastMonth() {
        List<Object[]> results = statisticsRepository.findTopRatedProductLastMonth();

        if (results == null || results.isEmpty()) {
            Map<String, Object> empty = new HashMap<>();
            empty.put("name", "Sin datos");
            empty.put("category", "N/A");
            empty.put("averageRating", 0.0);
            empty.put("averageRatingFormatted", "0.0");
            empty.put("reviewCount", 0);
            empty.put("imagePath", "/images/default-product.png");
            empty.put("exists", false);
            empty.put("recentReviews", new ArrayList<>()); // Empty list
            return empty;
        }

        Object[] row = results.get(0);
        Map<String, Object> product = new HashMap<>();
        Long productId = ((Number) row[0]).longValue(); // Product ID
        product.put("id", productId);
        product.put("name", row[1]);
        product.put("category", row[2]);

        double avgRating = ((Number) row[3]).doubleValue();
        product.put("averageRating", avgRating);
        product.put("averageRatingFormatted", String.format("%.1f", avgRating));
        product.put("reviewCount", ((Number) row[4]).longValue());

        // Get 3 most recent reviews for this product
        List<Review> recentReviews = reviewRepository.findTop3ByProductIdOrderByCreatedAtDesc(productId);

        // Convert reviews to a simple format for Mustache
        List<Map<String, Object>> reviewList = new ArrayList<>();
        for (Review review : recentReviews) {
            Map<String, Object> reviewMap = new HashMap<>();
            reviewMap.put("stars", review.getStars());
            reviewMap.put("text", review.getText());
            reviewMap.put("userName", review.getUser().getName());
            reviewMap.put("createdAt",
                    review.getCreatedAt() != null
                            ? java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy").format(review.getCreatedAt())
                            : "");
            reviewList.add(reviewMap);
        }
        product.put("recentReviews", reviewList);

        // Use ID to generate image path
        //product.put("imagePath", "/images/" + productId);
        Product productEntity = productRepository.findById(productId).orElse(null);
        product.put("imagePath", "/images/" + productEntity.getImage().getId());
        product.put("exists", true);

        return product;
    }

    /**
     * Get the top selling category from the last 3 months
     * @return Map with category details
     */
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

        // Category best seller (first in the list)
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

    /**
     * Get all categories with their sales data from the last 3 months
     * @return List of category maps with units, amount, percentage and color
     */
    public List<Map<String, Object>> getAllCategoriesLast3Months() {
        List<Object[]> results = statisticsRepository.findTopCategoryLast3Months();
        List<Map<String, Object>> categories = new ArrayList<>();

        if (results == null || results.isEmpty()) {
            return categories;
        }

        // Colors for categories
        Map<String, String> categoryColors = new HashMap<>();
        categoryColors.put("HOT", "#ff6b6b");
        categoryColors.put("COLD", "#4dabf7");
        categoryColors.put("BLENDED", "#9775fa");
        categoryColors.put("DESSERTS", "#ff8787");
        categoryColors.put("NON_COFFEE", "#69db7e");

        // Calculate total amount for percentages
        double totalAmount = 0;
        for (Object[] row : results) {
            totalAmount += ((Number) row[2]).doubleValue();
        }

        // Process every category
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

    /**
     * Get the top performing branch (by revenue)
     * @return Map with branch details including description and discount
     */
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
            empty.put("description", "");
            empty.put("discountPercent", 0);
            empty.put("exists", false);
            return empty;
        }

        Object[] row = results.get(0);
        Map<String, Object> branch = new HashMap<>();
        String branchName = (String) row[0];
        branch.put("name", branchName);
        branch.put("totalOrders", ((Number) row[1]).longValue());
        branch.put("totalUnits", ((Number) row[2]).longValue());

        double revenue = ((Number) row[3]).doubleValue();
        branch.put("totalRevenue", revenue);
        branch.put("totalRevenueFormatted", String.format("%.2f", revenue));

        double avgOrder = ((Number) row[4]).doubleValue();
        branch.put("avgOrderValue", String.format("%.2f", avgOrder));
        
        // Find the branch in the repository to get description and discount
        Branch branchEntity = branchRepository.findByName(branchName).orElse(null);
        if (branchEntity != null) {
            branch.put("description", branchEntity.getDescription());
            branch.put("discountPercent", branchEntity.getPurchaseDiscountPercent());
        } else {
            branch.put("description", "Sucursal destacada por su excelente rendimiento y servicio.");
            branch.put("discountPercent", 15); // Default discount
        }
        
        branch.put("exists", true);

        return branch;
    }

    /**
     * Get all branches with their sales data
     * @return List of branch maps with revenue, units, orders and percentage
     */
    public List<Map<String, Object>> getAllBranches() {
        List<Object[]> results = statisticsRepository.findAllBranchesSales();
        List<Map<String, Object>> branches = new ArrayList<>();

        if (results == null || results.isEmpty()) {
            return branches;
        }

        // Assign colors to branches
        String[] colors = { "#4263eb", "#9775fa", "#ff8787", "#69db7e", "#ffd43b", "#ff6b6b" };
        int colorIndex = 0;

        // Calculate total revenue first (handling nulls)
        double totalRevenue = 0;
        for (Object[] row : results) {
            Number revenue = (Number) row[3];
            totalRevenue += revenue != null ? revenue.doubleValue() : 0.0;
        }

        // Process every branch
        for (Object[] row : results) {
            Map<String, Object> branch = new LinkedHashMap<>();

            // Branch name
            branch.put("name", row[0] != null ? row[0] : "Sin nombre");

            // Total orders (may be null)
            Number orders = (Number) row[1];
            branch.put("orders", orders != null ? orders.longValue() : 0L);

            // Units sold (may be null)
            Number units = (Number) row[2];
            branch.put("units", units != null ? units.longValue() : 0L);

            // Total revenue (may be null)
            Number revenue = (Number) row[3];
            double revenueValue = revenue != null ? revenue.doubleValue() : 0.0;

            branch.put("revenue", revenueValue);
            branch.put("revenueFormatted", String.format("%.2f", revenueValue));

            // Calculate percentage
            double percentage = totalRevenue > 0 ? (revenueValue / totalRevenue) * 100 : 0;
            branch.put("percentage", Math.round(percentage));

            // Assign color
            branch.put("color", colors[colorIndex % colors.length]);
            colorIndex++;

            branches.add(branch);
        }

        return branches;
    }

    /**
     * Main method to get all chart data for the statistics page
     * @return Map containing all statistics data
     */
    public Map<String, Object> getChartStatistics() {
        Map<String, Object> stats = new HashMap<>();

        // Most sold product
        stats.put("bestProduct", getBestSellingProductCurrentMonth());

        // Top rated product (by stars)
        stats.put("topRatedProduct", getTopRatedProductLastMonth());

        // Categories
        stats.put("topCategory", getTopCategoryLast3Months());
        stats.put("allCategories", getAllCategoriesLast3Months());

        // Branches
        stats.put("topBranch", getTopBranch());
        stats.put("allBranches", getAllBranches());

        return stats;
    }

    /**
     * Get all product categories for filtering
     * @return List of category names
     */
    public List<String> getAllProductCategories() {
        return statisticsRepository.findAllProductCategories();
    }
}