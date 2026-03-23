package es.codeurjc.mokaf.api.mapper;

import es.codeurjc.mokaf.api.dto.OrderItemDTO;
import es.codeurjc.mokaf.model.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderItemMapper {

    @Mapping(target = "productId",   source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    OrderItemDTO toDto(OrderItem orderItem);
}