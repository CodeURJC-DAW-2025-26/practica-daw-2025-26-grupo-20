package es.codeurjc.mokaf.api.dto.statsdto;

import java.util.List;

public record ProductWithReviewsDTO(
    Long id,
    String name,
    String category,
    String imagePath,
    Boolean exists,
    Double averageRating,
    String averageRatingFormatted,
    Long reviewCount,
    List<ReviewStatDTO> recentReviews
) {}
