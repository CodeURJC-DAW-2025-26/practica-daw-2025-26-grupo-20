package es.codeurjc.mokaf.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import es.codeurjc.mokaf.model.Faq;

public interface FaqRepository extends JpaRepository<Faq, Long> {
}
