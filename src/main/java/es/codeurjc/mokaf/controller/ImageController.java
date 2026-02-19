package es.codeurjc.mokaf.controller;

import es.codeurjc.mokaf.model.Image;
import es.codeurjc.mokaf.repository.ImageRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Blob;

@RestController
public class ImageController {

    private final ImageRepository imageRepository;

    public ImageController(ImageRepository imageRepository) {
        this.imageRepository = imageRepository;
    }

    @GetMapping("/images/{id}")
    public ResponseEntity<byte[]> getImage(@PathVariable Long id) throws Exception {
        Image img = imageRepository.findById(id).orElseThrow();

        Blob blob = img.getImageFile();
        byte[] bytes = blob.getBytes(1, (int) blob.length());

        // Como tu entidad Image no guarda contentType, asumimos PNG (si son PNG en static)
        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(bytes);
    }
}
