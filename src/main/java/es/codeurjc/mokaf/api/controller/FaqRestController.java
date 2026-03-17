package es.codeurjc.mokaf.api.controller;

import es.codeurjc.mokaf.api.dto.FaqDTO;
import es.codeurjc.mokaf.model.Faq;
import es.codeurjc.mokaf.service.FaqService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/faqs")
public class FaqRestController {

    @Autowired
    private FaqService faqService;

    @Operation(summary = "Get all FAQs")
    @GetMapping
    public List<FaqDTO> getFaqs() {
        return faqService.getAllFaqs().stream()
                .map(FaqDTO::new)
                .collect(Collectors.toList());
    }

    @Operation(summary = "Get a FAQ by its id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the FAQ"),
            @ApiResponse(responseCode = "404", description = "FAQ not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<FaqDTO> getFaq(@PathVariable Long id) {
        return faqService.getFaqById(id)
                .map(f -> ResponseEntity.ok(new FaqDTO(f)))
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new FAQ")
    @PostMapping
    public ResponseEntity<FaqDTO> createFaq(@RequestBody FaqDTO faqDTO) {
        Faq faq = new Faq();
        faq.setQuestion(faqDTO.getQuestion());
        faq.setAnswer(faqDTO.getAnswer());

        Faq savedFaq = faqService.save(faq);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(savedFaq.getId()).toUri();

        return ResponseEntity.created(location).body(new FaqDTO(savedFaq));
    }

    @Operation(summary = "Update an existing FAQ")
    @PutMapping("/{id}")
    public ResponseEntity<FaqDTO> updateFaq(@PathVariable Long id, @RequestBody FaqDTO faqDTO) {
        return faqService.getFaqById(id)
                .map(faq -> {
                    faq.setQuestion(faqDTO.getQuestion());
                    faq.setAnswer(faqDTO.getAnswer());
                    Faq updatedFaq = faqService.save(faq);
                    return ResponseEntity.ok(new FaqDTO(updatedFaq));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Delete a FAQ")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFaq(@PathVariable Long id) {
        if (faqService.getFaqById(id).isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        faqService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
