package es.codeurjc.mokaf.api.dto.statsdto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ProductStatDTO(
    Long id,
    String name,
    String category,
    Long totalUnits,
    BigDecimal totalAmount,
    String totalAmountFormatted,
    String imagePath,
    Boolean exists
    //List<ReviewDTO> recentReviews
) {}