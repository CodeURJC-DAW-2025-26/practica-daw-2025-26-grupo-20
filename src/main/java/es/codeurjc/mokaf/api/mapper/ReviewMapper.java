package es.codeurjc.mokaf.api.mapper;

import es.codeurjc.mokaf.api.dto.ReviewDTO;
import es.codeurjc.mokaf.api.dto.UserBasicDTO;
import es.codeurjc.mokaf.api.dto.StatsDTO.ReviewStatDTO;
import es.codeurjc.mokaf.model.Review;
import es.codeurjc.mokaf.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "createdAtFormatted", expression = "java(review.getCreatedAtFormatted())")
    @Mapping(target = "productId", expression = "java(review.getProduct() != null ? review.getProduct().getId() : null)")
    ReviewDTO toDTO(Review review);

    List<ReviewDTO> toDTOs(Collection<Review> reviews);

    UserBasicDTO toDTO(User user);

    default ReviewStatDTO toReviewStatDTO(Map<String, Object> reviewMap) {
        if (reviewMap == null)
            return null;

        Integer stars = null;
        if (reviewMap.get("stars") instanceof Number) {
            stars = ((Number) reviewMap.get("stars")).intValue();
        }

        return new ReviewStatDTO(
                stars,
                (String) reviewMap.get("text"),
                (String) reviewMap.get("userName"),
                (String) reviewMap.get("createdAt"));
    }

    default ReviewDTO toDTOFromMap(Map<String, Object> reviewMap) {
        if (reviewMap == null)
            return null;

        Integer stars = null;
        if (reviewMap.get("stars") instanceof Number) {
            stars = ((Number) reviewMap.get("stars")).intValue();
        }

     
        return new ReviewDTO(
                null,
                null, 
                stars != null ? stars : 0,
                (String) reviewMap.get("text"),
                (String) reviewMap.get("createdAt"),
                null
        );
    }

}