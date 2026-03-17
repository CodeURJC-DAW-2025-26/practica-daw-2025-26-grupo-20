package es.codeurjc.mokaf.api.interfaces;

import java.time.LocalDateTime;

public interface UserDTO {
    Long getId();
    void setId(Long id);
    String getName();
    void setName(String name);
    String getEmail();
    void setEmail(String email);
    String getRole();
    void setRole(String role);
    Long getImageId();
    void setImageId(Long imageId);
    LocalDateTime getCreatedAt();
    void setCreatedAt(LocalDateTime createdAt);
}
