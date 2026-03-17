package es.codeurjc.mokaf.dto;

import es.codeurjc.mokaf.dto.ReviewDTO;
import es.codeurjc.mokaf.dto.UserBasicDTO;
import es.codeurjc.mokaf.model.Review;
import es.codeurjc.mokaf.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {

    @Mapping(target = "createdAtFormatted", expression = "java(review.getCreatedAtFormatted())")
    @Mapping(target = "productId", expression = "java(review.getProduct() != null ? review.getProduct().getId() : null)")
    ReviewDTO toDTO(Review review);

    List<ReviewDTO> toDTOs(Collection<Review> reviews);

    UserBasicDTO toDTO(User user);
}