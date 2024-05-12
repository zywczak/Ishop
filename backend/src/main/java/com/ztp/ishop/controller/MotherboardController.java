package com.ztp.ishop.controller;

import com.ztp.ishop.entity.Motherboard;
import com.ztp.ishop.entity.Product;
import com.ztp.ishop.repository.MotherboardRepository;
import com.ztp.ishop.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/motherboards")
public class MotherboardController {

    @Autowired
    private MotherboardRepository motherboardRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public List<Motherboard> getAllMotherboards() {
        List<Motherboard> motherboards = motherboardRepository.findAll();
        for (Motherboard motherboard : motherboards) {
            if (motherboard.getProduct() != null) {
                motherboard.getProduct().setPhoto("http://localhost:8080/motherboards/" + motherboard.getId() + "/photo");
            }
        }
        return motherboards;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Motherboard> getMotherboard(@PathVariable Long id) {
        Motherboard motherboard = motherboardRepository.findById(id).orElse(null);
        if (motherboard != null) {
            if (motherboard.getProduct() != null) {
                motherboard.getProduct().setPhoto("http://localhost:8080/motherboards/" + id + "/photo");
            }
            return ResponseEntity.ok(motherboard);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public void deleteMotherboard(@PathVariable Long id) {
        motherboardRepository.findById(id).ifPresent(motherboard -> {
            Product product = motherboard.getProduct();
            if (product != null) {
                String photoPath = product.getPhoto();
                if (photoPath != null && !photoPath.isEmpty()) {
                    String filePath = "C:\\Users\\pjzyw\\OneDrive\\Pulpit\\ztp\\backend\\src\\main\\resources\\public\\" + photoPath; 
                    File photoFile = new File(filePath);
                    if (photoFile.exists()) {
                        photoFile.delete();
                    }
                }

                motherboard.setProduct(null);
                productRepository.delete(product);
            }
            motherboardRepository.delete(motherboard);
        });
    }

    @PutMapping("/{id}")
    public Motherboard updateMotherboard(@PathVariable Long id, @RequestBody Motherboard updatedMotherboard) {
        return motherboardRepository.findById(id)
                .map(motherboard -> {
                    motherboard.setChipset(updatedMotherboard.getChipset());
                    motherboard.setFormFactor(updatedMotherboard.getFormFactor());
                    motherboard.setSupportedMemory(updatedMotherboard.getSupportedMemory());
                    motherboard.setSocket(updatedMotherboard.getSocket());
                    motherboard.setCpuArchitecture(updatedMotherboard.getCpuArchitecture());
                    motherboard.setInternalConnectors(updatedMotherboard.getInternalConnectors());
                    motherboard.setExternalConnectors(updatedMotherboard.getExternalConnectors());
                    motherboard.setMemorySlots(updatedMotherboard.getMemorySlots());
                    motherboard.setAudioSystem(updatedMotherboard.getAudioSystem());

                    Product product = motherboard.getProduct();
                    if (product != null) {
                        product.setManufacturer(updatedMotherboard.getProduct().getManufacturer());
                        product.setModel(updatedMotherboard.getProduct().getModel());
                        product.setPrice(updatedMotherboard.getProduct().getPrice());
                        product.setPhoto(motherboard.getProduct().getPhoto());
                    }

                    return motherboardRepository.save(motherboard);
                })
                .orElseThrow(() -> new IllegalArgumentException("Motherboard not found with id " + id));
    }

    @PostMapping
    public Motherboard addMotherboard(@RequestBody Motherboard newMotherboard) {
        Product product = newMotherboard.getProduct();
        if (product != null) {
            product.setPhoto("");
            product = productRepository.save(product);
            newMotherboard.setProduct(product);
        }

        Motherboard savedMotherboard = motherboardRepository.save(newMotherboard);
        return savedMotherboard;
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getMotherboardPhoto(@PathVariable Long id) {
        return (ResponseEntity<byte[]>) motherboardRepository.findById(id)
            .map(motherboard -> {
                Product product = motherboard.getProduct();
                if (product != null) {
                    try {
                        String fileName;
                        if(product.getPhoto() == ""){
                            fileName = "brakfoto.png";
                        } else {
                            fileName = product.getPhoto();
                        }
                        
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
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error reading photo: " + e.getMessage());
                    }
                } else {
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product photo not found for motherboard with id: " + id);
                }
            })
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Motherboard not found with id: " + id));
    }


    @DeleteMapping("/{id}/photo")
    public ResponseEntity<Object> deleteMotherboardPhoto(@PathVariable Long id) {
        return motherboardRepository.findById(id)
            .map(motherboard -> {
                Product product = motherboard.getProduct();
                if (product != null && product.getPhoto() != null && !product.getPhoto().isEmpty()) {
                    String photoPath = "C:\\Users\\pjzyw\\OneDrive\\Pulpit\\ztp\\backend\\src\\main\\resources\\public\\" + product.getPhoto(); 
                    File photoFile = new File(photoPath);
                    if (photoFile.exists()) {
                        photoFile.delete();
                    }
                    product.setPhoto("");
                    productRepository.save(product);
                    return ResponseEntity.ok().build();
                } else {
                    return ResponseEntity.notFound().build();
                }
            })
            .orElse(ResponseEntity.notFound().build());
    }

    @SuppressWarnings("unchecked")
    @PutMapping("/{id}/photo")
    public ResponseEntity<Object> updateMotherboardPhoto(@PathVariable Long id, @RequestParam("photo") MultipartFile photoFile) {
        if (photoFile.isEmpty()) {
            return ResponseEntity.badRequest().body("No photo provided.");
        }

        return (ResponseEntity<Object>) motherboardRepository.findById(id)
            .map(motherboard -> {
                Product product = motherboard.getProduct();
                if (product != null) {
                    String oldPhotoPath = product.getPhoto();
                    if (oldPhotoPath != null && !oldPhotoPath.isEmpty()) {
                        String oldFilePath = "C:\\Users\\pjzyw\\OneDrive\\Pulpit\\ztp\\backend\\src\\main\\resources\\public\\" + oldPhotoPath; 
                        File oldPhotoFile = new File(oldFilePath);
                        if (oldPhotoFile.exists()) {
                            oldPhotoFile.delete();
                        }
                    }

                    String newPhotoPath = savePhoto(photoFile);
                    product.setPhoto(newPhotoPath);
                    productRepository.save(product);
                    return ResponseEntity.ok("Motherboard photo updated successfully.");
                } else {
                    return ResponseEntity.notFound().build();
                }
            })
            .orElse(ResponseEntity.notFound().build());
    }

    private String savePhoto(MultipartFile photoFile) {
        String photoDirectoryPath = "C:\\Users\\pjzyw\\OneDrive\\Pulpit\\ztp\\backend\\src\\main\\resources\\public\\";

        String originalFileName = photoFile.getOriginalFilename();

        String fileExtension = originalFileName.substring(originalFileName.lastIndexOf(".") + 1).toLowerCase();

        String fileName = "photo_" + System.currentTimeMillis() + "." + fileExtension;

        File newPhotoFile = new File(photoDirectoryPath + fileName);

        try {
            photoFile.transferTo(newPhotoFile);

            return fileName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
