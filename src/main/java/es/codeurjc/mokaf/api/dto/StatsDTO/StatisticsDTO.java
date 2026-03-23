package es.codeurjc.mokaf.api.dto.StatsDTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record StatisticsDTO(
    ProductStatDTO bestProduct,
    ProductStatDTO topRatedProduct,
    CategoryStatDTO topCategory,
    List<CategoryStatDTO> allCategories,
    BranchStatDTO topBranch,
    List<BranchStatDTO> allBranches
) {}