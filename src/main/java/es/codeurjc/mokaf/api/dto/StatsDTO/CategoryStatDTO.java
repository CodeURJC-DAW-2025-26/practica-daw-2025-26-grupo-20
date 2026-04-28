package es.codeurjc.mokaf.api.dto.statsdto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record CategoryStatDTO(
    String category,
    Long units,
    BigDecimal amount,
    String amountFormatted,
    Long orderCount,
    Integer percentage,
    String color,
    Boolean exists
) {}