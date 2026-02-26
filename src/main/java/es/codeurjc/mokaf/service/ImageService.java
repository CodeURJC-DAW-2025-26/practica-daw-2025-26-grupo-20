package es.codeurjc.mokaf.service;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialBlob;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import es.codeurjc.mokaf.model.Image;
import es.codeurjc.mokaf.repository.ImageRepository;

@Service
public class ImageService {

    @Autowired
    private ImageRepository imageRepository;

    /**
     * Create new image from MultipartFile
     */
    public Image createImage(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        try {
            byte[] bytes = file.getBytes();
            Blob blob = new SerialBlob(bytes);
            
            Image image = new Image();
            image.setImageFile(blob);
            
            return imageRepository.save(image);
        } catch (SQLException e) {
            throw new IOException("Error creating blob from file", e);
        }
    }

    /**
     * Update existing image or create new one
     */
    public Image updateImage(Long existingImageId, MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) {
            return null;
        }
        
        // If there's an existing image, delete it first
        if (existingImageId != null) {
            imageRepository.deleteById(existingImageId);
        }
        
        return createImage(file);
    }

    public Optional<Image> findById(Long id) {
        return imageRepository.findById(id);
    }

    public void deleteImage(Long id) {
        if (id != null) {
            imageRepository.deleteById(id);
        }
    }

    public List<Image> findAllprofilImages() {
        return imageRepository.findAll();
    }

    public Optional<Image> findByIdWithUser(Long id) {
        return imageRepository.findById(id);
    }
}