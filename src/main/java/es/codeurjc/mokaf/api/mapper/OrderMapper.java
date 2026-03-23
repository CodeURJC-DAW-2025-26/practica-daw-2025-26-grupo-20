package es.codeurjc.mokaf.api.mapper;

import es.codeurjc.mokaf.api.dto.OrderDTO;
import es.codeurjc.mokaf.model.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {OrderItemMapper.class})
public interface OrderMapper {

    @Mapping(target = "userId",     source = "user.id")
    @Mapping(target = "userEmail",  source = "user.email")
    @Mapping(target = "branchId",   source = "branch.id")
    @Mapping(target = "branchName", source = "branch.name")
    @Mapping(target = "status",     expression = "java(order.getStatus().name())")
    OrderDTO toDto(Order order);
}