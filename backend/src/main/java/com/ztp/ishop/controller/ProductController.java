package com.ztp.ishop.controller;

import com.ztp.ishop.repository.ProductRepository;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/products")
public class ProductController {

    private final ProductRepository productRepository;

    public ProductController(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getProductPhoto(@PathVariable Long id) {
        return productRepository.findById(id)
            .map(product -> {
                try {
                    String fileName = (product.getPhoto() == null || product.getPhoto().isEmpty()) 
                                      ? "brakfoto.png" 
                                      : product.getPhoto();
                    
                    String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
                    Resource resource = new ClassPathResource("public/" + fileName);
                    byte[] photoBytes = Files.readAllBytes(resource.getFile().toPath());

                    MediaType mediaType;
                    switch (fileExtension) {
                        case "jpg":
                        case "jpeg":
                            mediaType = MediaType.IMAGE_JPEG;
                            break;
                        case "png":
                            mediaType = MediaType.IMAGE_PNG;
                            break;
                        default:
                            mediaType = MediaType.APPLICATION_OCTET_STREAM;
                            break;
                    }

                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(mediaType); 
                    headers.setContentLength(photoBytes.length);

                    return new ResponseEntity<>(photoBytes, headers, HttpStatus.OK);
                } catch (IOException e) {
                    e.printStackTrace();
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                         .body(("Error reading photo: " + e.getMessage()).getBytes());
                }
            })
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                                  .body(("Product not found with id: " + id).getBytes()));
    }


     // private String savePhoto(MultipartFile photoFile) {
    //     String photoDirectoryPath = "C:\\Users\\pjzyw\\OneDrive\\Pulpit\\ztp\\backend\\src\\main\\resources\\public\\";

    //     String originalFileName = photoFile.getOriginalFilename();

    //     String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();

    //     String fileName = "photo_" + System.currentTimeMillis() + "." + fileExtension;

    //     File newPhotoFile = new File(photoDirectoryPath + fileName);

    //     try {
    //         photoFile.transferTo(newPhotoFile);

    //         return fileName;
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //         return null;
    //     }
    // }
}
