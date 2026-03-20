package es.codeurjc.mokaf.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO(
        Long id,
        Long userId,
        String userEmail,
        Long branchId,
        String branchName,
        String status,
        BigDecimal subtotalAmount,
        BigDecimal discountPercent,
        BigDecimal discountAmount,
        BigDecimal totalAmount,
        LocalDateTime createdAt,
        LocalDateTime paidAt,
        List<OrderItemDTO> items
) {}