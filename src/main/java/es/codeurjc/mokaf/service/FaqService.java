package es.codeurjc.mokaf.service;

import es.codeurjc.mokaf.model.Faq;
import es.codeurjc.mokaf.repository.FaqRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FaqService {

    private final FaqRepository faqRepository;

    public FaqService(FaqRepository faqRepository) {
        this.faqRepository = faqRepository;
    }

    public List<Faq> getAllFaqs() {
        return faqRepository.findAll();
    }

    public java.util.Optional<Faq> getFaqById(Long id) {
        return faqRepository.findById(id);
    }

    public Faq save(Faq faq) {
        return faqRepository.save(faq);
    }

    public void delete(Long id) {
        faqRepository.deleteById(id);
    }
}
