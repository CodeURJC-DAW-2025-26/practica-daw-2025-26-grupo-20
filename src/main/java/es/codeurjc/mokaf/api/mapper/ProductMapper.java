package es.codeurjc.mokaf.api.mapper;

import es.codeurjc.mokaf.api.dto.ProductDTO;
import es.codeurjc.mokaf.model.Category;
import es.codeurjc.mokaf.model.Product;
import org.mapstruct.*;

import java.util.Arrays;

@Mapper(componentModel = "spring", uses = {AllergenMapper.class})
public interface ProductMapper {

    @Mapping(target = "category", expression = "java(product.getCategory() != null ? product.getCategory().name() : null)")
    @Mapping(target = "imageId", source = "image.id")
    ProductDTO toDTO(Product product);

    @Mapping(target = "category", ignore = true)
    Product toEntity(ProductDTO dto);

    @Mapping(target = "category", ignore = true)
    void updateEntity(@MappingTarget Product product, ProductDTO dto);

    @AfterMapping
    default void mapCategoryToEntity(ProductDTO dto, @MappingTarget Product product) {
        if (dto.category() != null) {
            try {
                product.setCategory(Category.valueOf(dto.category().toUpperCase().trim()));
            } catch (IllegalArgumentException e) {
                String allowed = Arrays.toString(Category.values());
                throw new IllegalArgumentException("Invalid category: '" + dto.category()
                        + "'. Allowed values are: " + allowed);
            }
        }
    }
}
