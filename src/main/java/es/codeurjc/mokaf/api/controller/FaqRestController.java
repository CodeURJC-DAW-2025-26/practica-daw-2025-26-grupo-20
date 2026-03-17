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
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/faqs")
public class FaqRestController {

    @Autowired
    private FaqService faqService;

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
        return faqService.getFaqById(id)
                .map(faqMapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("FAQ with id " + id + " not found"));
    }

    @Operation(summary = "Create a new FAQ")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FaqDTO createFaq(@Valid @RequestBody FaqDTO faqDTO) {
        Faq faq = faqMapper.toEntity(faqDTO);
        Faq savedFaq = faqService.save(faq);
        return faqMapper.toDTO(savedFaq);
    }

    @Operation(summary = "Update an existing FAQ")
    @PutMapping("/{id}")
    public FaqDTO updateFaq(@PathVariable Long id, @Valid @RequestBody FaqDTO faqDTO) {
        return faqService.getFaqById(id)
                .map(faq -> {
                    faqMapper.updateEntity(faq, faqDTO);
                    Faq updatedFaq = faqService.save(faq);
                    return faqMapper.toDTO(updatedFaq);
                })
                .orElseThrow(() -> new ResourceNotFoundException("FAQ with id " + id + " not found"));
    }

    @Operation(summary = "Delete a FAQ")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFaq(@PathVariable Long id) {
        if (faqService.getFaqById(id).isEmpty()) {
            throw new ResourceNotFoundException("FAQ with id " + id + " not found");
        }
        faqService.delete(id);
    }
}
