package es.codeurjc.mokaf.api.dto.StatsDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record BranchStatDTO(
    // Datos básicos de la sucursal
    String name,
    String description,
    BigDecimal discountPercent,
    
    // Datos de estadísticas
    Long totalOrders,
    Long totalUnits,
    BigDecimal totalRevenue,
    String totalRevenueFormatted,
    String avgOrderValue,
    
    // Datos para gráficos
    Long orders,
    Long units,
    BigDecimal revenue,
    String revenueFormatted,
    Integer percentage,
    String color,
    
    // Flag de existencia
    Boolean exists
) {}