package es.codeurjc.mokaf.api.mapper;

import es.codeurjc.mokaf.api.dto.CartDTOs.CartItemDTO;
import es.codeurjc.mokaf.api.dto.CartDTOs.CartResponseDTO;
import es.codeurjc.mokaf.api.dto.CartDTOs.CartSummaryDTO;
import es.codeurjc.mokaf.model.OrderItem;
import es.codeurjc.mokaf.service.CartService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "name", source = "product.name")
    @Mapping(target = "imageUrl", source = "product.imageUrl")
    CartItemDTO toCartItemDTO(OrderItem item);

    List<CartItemDTO> toCartItemDTOs(List<OrderItem> items);

    default CartSummaryDTO toCartSummaryDTO(CartService.CartSummary summary, int totalUnits) {
        if (summary == null || summary.getCart() == null) {
            return emptyCartSummary();
        }

        List<CartItemDTO> items = toCartItemDTOs(summary.getCart().getItems());

        return new CartSummaryDTO(
            items,
            summary.getSubtotal(),
            summary.getTax(),
            summary.getTotal(),
            summary.getDiscountInfo(),
            summary.hasDiscount(),
            summary.getItemCount(),
            totalUnits
        );
    }

    default CartSummaryDTO emptyCartSummary() {
        return new CartSummaryDTO(
            List.of(), "0.00€", "0.00€", "0.00€", null, false, 0, 0
        );
    }

    default CartResponseDTO successResponse(String message, CartSummaryDTO cartSummary, Integer cartCount) {
        return new CartResponseDTO(true, message, cartSummary, cartCount, null);
    }

    default CartResponseDTO successResponseSimple(String message) {
        return new CartResponseDTO(true, message, emptyCartSummary(), 0, null);
    }
}