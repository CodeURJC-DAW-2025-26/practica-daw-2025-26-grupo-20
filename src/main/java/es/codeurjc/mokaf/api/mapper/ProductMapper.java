package es.codeurjc.mokaf.api.mapper;

import es.codeurjc.mokaf.api.dto.AllergenDTO;
import es.codeurjc.mokaf.api.dto.ProductDTO;
import es.codeurjc.mokaf.api.dto.ProductDetailDTO;
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

    @Mapping(target = "imageId", expression = "java(product.getImage() != null ? product.getImage().getId() : null)")
    @Mapping(target = "category", expression = "java(product.getCategory() != null ? product.getCategory().name() : null)")
    @Mapping(target = "imageFile", ignore = true)
    ProductDTO toProductDTO(Product product);

    List<ProductDTO> toProductDTOs(Collection<Product> products);

    AllergenDTO toDTO(Allergen allergen);

    Set<AllergenDTO> toDTOs(Set<Allergen> allergens);

    List<AllergenDTO> toDTOs(List<Allergen> allergens);
}
