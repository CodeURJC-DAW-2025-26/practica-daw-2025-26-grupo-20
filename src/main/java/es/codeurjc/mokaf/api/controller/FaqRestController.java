package es.codeurjc.mokaf.api.controller;

import es.codeurjc.mokaf.api.dto.FaqDTO;
import es.codeurjc.mokaf.api.mapper.FaqMapper;
import es.codeurjc.mokaf.model.Faq;
import es.codeurjc.mokaf.service.FaqService;
import es.codeurjc.mokaf.api.exception.ResourceNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping("/api/v1/faqs")
public class FaqRestController {

    private final FaqService faqService;
    private final FaqMapper faqMapper;

    public FaqRestController(FaqService faqService, FaqMapper faqMapper) {
        this.faqService = faqService;
        this.faqMapper = faqMapper;
    }

    @Autowired
    private FaqMapper faqMapper;

    @Operation(summary = "Get all FAQs")
    @GetMapping
    public List<FaqDTO> getFaqs() {
        return faqService.getAllFaqs().stream()
                .map(faqMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get a FAQ by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the FAQ"),
            @ApiResponse(responseCode = "404", description = "FAQ not found")
    })
    @GetMapping("/{id}")
    public FaqDTO getFaq(@PathVariable Long id) {
        Faq faq = faqService.getFaqById(id)
                .orElseThrow(() -> new NoSuchElementException("FAQ not found: " + id));

        return faqMapper.toDto(faq);
    }

    @Operation(summary = "Create a new FAQ")
    @PostMapping
    public FaqDTO createFaq(@RequestBody FaqDTO faqDTO) {
        Faq faq = faqMapper.toEntity(faqDTO);
        Faq savedFaq = faqService.save(faq);
        return faqMapper.toDto(savedFaq);
    }

    @Operation(summary = "Update an existing FAQ")
    @PutMapping("/{id}")
    public FaqDTO updateFaq(@PathVariable Long id, @RequestBody FaqDTO faqDTO) {
        Faq faq = faqService.getFaqById(id)
                .orElseThrow(() -> new NoSuchElementException("FAQ not found: " + id));

        faq.setQuestion(faqDTO.question());
        faq.setAnswer(faqDTO.answer());

        Faq updatedFaq = faqService.save(faq);
        return faqMapper.toDto(updatedFaq);
    }

    @Operation(summary = "Delete a FAQ")
    @DeleteMapping("/{id}")
    public void deleteFaq(@PathVariable Long id) {
        Faq faq = faqService.getFaqById(id)
                .orElseThrow(() -> new NoSuchElementException("FAQ not found: " + id));

        faqService.delete(faq.getId());
    }
}