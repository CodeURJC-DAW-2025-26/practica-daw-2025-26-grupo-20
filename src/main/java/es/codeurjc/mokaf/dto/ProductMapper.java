package es.codeurjc.mokaf.dto;

import es.codeurjc.mokaf.dto.AllergenDTO;
import es.codeurjc.mokaf.dto.ProductDetailDTO;
import es.codeurjc.mokaf.model.Allergen;
import es.codeurjc.mokaf.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "imageUrl", expression = "java(product.getImageUrl())")
    @Mapping(target = "category", expression = "java(product.getCategory() != null ? product.getCategory().name() : null)")
    ProductDetailDTO toDTO(Product product);

    List<ProductDetailDTO> toDTOs(Collection<Product> products);

    AllergenDTO toDTO(Allergen allergen);

    Set<AllergenDTO> toDTOs(Set<Allergen> allergens);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "image", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "reviews", ignore = true)
    @Mapping(target = "active", ignore = true)
    @Mapping(target = "allergens", ignore = true)
    @Mapping(target = "category", ignore = true)
    Product toDomain(ProductDetailDTO productDTO); //esta incommpleto
}