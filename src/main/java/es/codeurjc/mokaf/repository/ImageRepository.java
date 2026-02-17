package es.codeurjc.mokaf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc.mokaf.model.Image;

public interface ImageRepository extends JpaRepository<Image, Long> {}
