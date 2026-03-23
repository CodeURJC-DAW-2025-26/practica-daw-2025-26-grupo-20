package es.codeurjc.mokaf.api.dto.StatsDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BranchStatDTO(
    
    String name,
    String description,
    BigDecimal discountPercent,
    
   
    Long totalOrders,
    Long totalUnits,
    BigDecimal totalRevenue,
    String totalRevenueFormatted,
    String avgOrderValue,
    
  
    Long orders,
    Long units,
    BigDecimal revenue,
    String revenueFormatted,
    Integer percentage,
    String color,
    
   
    Boolean exists
) {}