package com.ztp.ishop.controller;

import com.ztp.ishop.entity.RAM;
import com.ztp.ishop.entity.Product;
import com.ztp.ishop.repository.RamRepository;
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
@RequestMapping("/rams")
public class RamController {

    @Autowired
    private RamRepository ramRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public List<RAM> getAllRAMs() {
        List<RAM> rams = ramRepository.findAll();
        for (RAM ram : rams) {
            if (ram.getProduct() != null) {
                ram.getProduct().setPhoto("http://localhost:8080/products/" + ram.getProduct().getId() + "/photo");
            }
        }
        return rams;
    }

    @GetMapping("/ram/{id}")
    public ResponseEntity<RAM> getRAM(@PathVariable Long id) {
        RAM ram = ramRepository.findById(id).orElse(null);
        if (ram != null) {
            if (ram.getProduct() != null) {
                ram.getProduct().setPhoto("http://localhost:8080/products/" + ram.getProduct().getId() + "/photo");
            }
            return ResponseEntity.ok(ram);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public void deleteRAM(@PathVariable Long id) {
        ramRepository.findById(id).ifPresent(ram -> {
            Product product = ram.getProduct();
            if (product != null) {
                String photoPath = product.getPhoto();
                if (photoPath != null && !photoPath.isEmpty()) {
                    String filePath = "C:\\Users\\pjzyw\\OneDrive\\Pulpit\\ztp\\backend\\src\\main\\resources\\public\\" + photoPath; 
                    File photoFile = new File(filePath);
                    if (photoFile.exists()) {
                        photoFile.delete();
                    }
                }
                ram.setProduct(null);
                productRepository.delete(product);
            }
            ramRepository.delete(ram);
        });
    }

    @PutMapping("/{id}")
    public RAM updateRAM(@PathVariable Long id, @RequestBody RAM updatedRAM) {
        return ramRepository.findById(id)
            .map(ram -> {
                ram.setSpeed(updatedRAM.getSpeed());
                ram.setCapacity(updatedRAM.getCapacity());
                ram.setVoltage(updatedRAM.getVoltage());
                ram.setModuleCount(updatedRAM.getModuleCount());
                ram.setBacklight(updatedRAM.isBacklight());
                ram.setCooling(updatedRAM.isCooling());

                Product product = ram.getProduct();
                if (product != null) {
                    product.setManufacturer(updatedRAM.getProduct().getManufacturer());
                    product.setModel(updatedRAM.getProduct().getModel());
                    product.setPrice(updatedRAM.getProduct().getPrice());
                    product.setPhoto(ram.getProduct().getPhoto());
                }

                return ramRepository.save(ram);
            })
            .orElseThrow(() -> new IllegalArgumentException("RAM not found with id " + id));
    }

    @PostMapping
    public RAM addRAM(@RequestBody RAM newRAM) {
        Product product = newRAM.getProduct();
        if (product != null) {
            product.setPhoto("");
            product = productRepository.save(product);
            newRAM.setProduct(product);
        }

        RAM savedRAM = ramRepository.save(newRAM);
        return savedRAM;
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getRAMPhoto(@PathVariable Long id) {
        return (ResponseEntity<byte[]>) ramRepository.findById(id)
            .map(ram -> {
                Product product = ram.getProduct();
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
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product photo not found for RAM with id: " + id);
                }
            })
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("RAM not found with id: " + id));
    }


    @DeleteMapping("/{id}/photo")
    public ResponseEntity<Object> deleteRAMPhoto(@PathVariable Long id) {
        return ramRepository.findById(id)
            .map(ram -> {
                Product product = ram.getProduct();
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
    public ResponseEntity<Object> updateRAMPhoto(@PathVariable Long id, @RequestParam("photo") MultipartFile photoFile) {
        if (photoFile.isEmpty()) {
            return ResponseEntity.badRequest().body("No photo provided.");
        }

        return (ResponseEntity<Object>) ramRepository.findById(id)
            .map(ram -> {
                Product product = ram.getProduct();
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
                    return ResponseEntity.ok("RAM photo updated successfully.");
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