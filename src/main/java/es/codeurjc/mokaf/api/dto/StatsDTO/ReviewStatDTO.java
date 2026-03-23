package es.codeurjc.mokaf.api.dto.StatsDTO;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ReviewStatDTO(
    Integer stars,
    String text,
    String userName,
    String createdAt
) {}