package es.codeurjc.mokaf.api.dto;

import es.codeurjc.mokaf.model.User;
import java.time.LocalDateTime;

public class UserDTO implements es.codeurjc.mokaf.api.interfaces.UserInterface {

    private Long id;
    private String name;
    private String email;
    private String role;
    private Long imageId;
    private LocalDateTime createdAt;

    public UserDTO() {
    }

    public UserDTO(User user) {
        this.id = user.getId();
        this.name = user.getName();
        this.email = user.getEmail();
        this.role = user.getRole() != null ? user.getRole().name() : null;
        this.imageId = user.getImage() != null ? user.getImage().getId() : null;
        this.createdAt = user.getCreatedAt();
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getEmail() {
        return email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public void setRole(String role) {
        this.role = role;
    }

    @Override
    public Long getImageId() {
        return imageId;
    }

    @Override
    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    @Override
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
