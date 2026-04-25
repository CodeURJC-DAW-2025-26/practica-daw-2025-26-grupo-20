package es.codeurjc.mokaf.api.mapper;

import es.codeurjc.mokaf.api.dto.statsdto.ProductStatDTO;
import es.codeurjc.mokaf.api.dto.statsdto.CategoryStatDTO;
import es.codeurjc.mokaf.api.dto.statsdto.BranchStatDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface StatisticsMapper {


    @Mapping(target = "id", expression = "java(getLong(productMap, \"id\"))")
    @Mapping(target = "name", expression = "java(getString(productMap, \"name\"))")
    @Mapping(target = "category", expression = "java(getString(productMap, \"category\"))")
    @Mapping(target = "totalUnits", expression = "java(getLong(productMap, \"totalUnits\"))")
    @Mapping(target = "totalAmount", expression = "java(getBigDecimal(productMap, \"totalAmount\"))")
    @Mapping(target = "totalAmountFormatted", expression = "java(getString(productMap, \"totalAmountFormatted\"))")
    @Mapping(target = "imagePath", expression = "java(getString(productMap, \"imagePath\"))")
    @Mapping(target = "exists", expression = "java(getBoolean(productMap, \"exists\"))")
    ProductStatDTO toProductStatDTO(Map<String, Object> productMap);

 
    @Mapping(target = "category", expression = "java(getString(categoryMap, \"category\"))")
    @Mapping(target = "units", expression = "java(getLong(categoryMap, \"units\"))")
    @Mapping(target = "amount", expression = "java(getBigDecimal(categoryMap, \"amount\"))")
    @Mapping(target = "amountFormatted", expression = "java(getString(categoryMap, \"amountFormatted\"))")
    @Mapping(target = "orderCount", expression = "java(getLong(categoryMap, \"orderCount\"))")
    @Mapping(target = "percentage", expression = "java(getInteger(categoryMap, \"percentage\"))")
    @Mapping(target = "color", expression = "java(getString(categoryMap, \"color\"))")
    @Mapping(target = "exists", expression = "java(getBoolean(categoryMap, \"exists\"))")
    CategoryStatDTO toCategoryStatDTO(Map<String, Object> categoryMap);


    @Mapping(target = "name", expression = "java(getString(branchMap, \"name\"))")
    @Mapping(target = "description", expression = "java(getString(branchMap, \"description\"))")
    @Mapping(target = "discountPercent", expression = "java(getBigDecimal(branchMap, \"discountPercent\"))")
    @Mapping(target = "totalOrders", expression = "java(getLong(branchMap, \"totalOrders\"))")
    @Mapping(target = "totalUnits", expression = "java(getLong(branchMap, \"totalUnits\"))")
    @Mapping(target = "totalRevenue", expression = "java(getBigDecimal(branchMap, \"totalRevenue\"))")
    @Mapping(target = "totalRevenueFormatted", expression = "java(getString(branchMap, \"totalRevenueFormatted\"))")
    @Mapping(target = "avgOrderValue", expression = "java(getString(branchMap, \"avgOrderValue\"))")
    @Mapping(target = "orders", expression = "java(getLong(branchMap, \"orders\"))")
    @Mapping(target = "units", expression = "java(getLong(branchMap, \"units\"))")
    @Mapping(target = "revenue", expression = "java(getBigDecimal(branchMap, \"revenue\"))")
    @Mapping(target = "revenueFormatted", expression = "java(getString(branchMap, \"revenueFormatted\"))")
    @Mapping(target = "percentage", expression = "java(getInteger(branchMap, \"percentage\"))")
    @Mapping(target = "color", expression = "java(getString(branchMap, \"color\"))")
    @Mapping(target = "exists", expression = "java(getBoolean(branchMap, \"exists\"))")
    BranchStatDTO toBranchStatDTO(Map<String, Object> branchMap);


    
    default Long getLong(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) return null;
        Object value = map.get(key);
        if (value instanceof Number) return ((Number) value).longValue();
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    default Double getDouble(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) return null;
        Object value = map.get(key);
        if (value instanceof Number) return ((Number) value).doubleValue();
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    default java.math.BigDecimal getBigDecimal(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) return null;
        Object value = map.get(key);
        if (value instanceof java.math.BigDecimal) return (java.math.BigDecimal) value;
        if (value instanceof Number) return java.math.BigDecimal.valueOf(((Number) value).doubleValue());
        if (value instanceof String) {
            try {
                return new java.math.BigDecimal((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    default Integer getInteger(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) return null;
        Object value = map.get(key);
        if (value instanceof Number) return ((Number) value).intValue();
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    default String getString(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) return null;
        Object value = map.get(key);
        return value != null ? value.toString() : null;
    }

    default Boolean getBoolean(Map<String, Object> map, String key) {
        if (map == null || !map.containsKey(key)) return null;
        Object value = map.get(key);
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof String) return Boolean.parseBoolean((String) value);
        return null;
    }


    default List<CategoryStatDTO> toCategoryStatDTOs(List<Map<String, Object>> categoryMaps) {
        if (categoryMaps == null) return List.of();
        return categoryMaps.stream()
                .map(this::toCategoryStatDTO)
                .collect(Collectors.toList());
    }

    default List<BranchStatDTO> toBranchStatDTOs(List<Map<String, Object>> branchMaps) {
        if (branchMaps == null) return List.of();
        return branchMaps.stream()
                .map(this::toBranchStatDTO)
                .collect(Collectors.toList());
    }
}