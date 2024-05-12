package com.ztp.ishop.controller;

import com.ztp.ishop.entity.CPU;
import com.ztp.ishop.entity.Product;
import com.ztp.ishop.repository.CpuRepository;
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
@RequestMapping("/cpus")
public class CpuController {

    @Autowired
    private CpuRepository cpuRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public List<CPU> getAllCPUs() {
        List<CPU> cpus = cpuRepository.findAll();
        for (CPU cpu : cpus) {
            if (cpu.getProduct() != null) {
                cpu.getProduct().setPhoto("http://localhost:8080/cpus/" + cpu.getId() + "/photo");
            }
        }
        return cpus;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CPU> getCPU(@PathVariable Long id) {
        CPU cpu = cpuRepository.findById(id).orElse(null);
        if (cpu != null) {
            if (cpu.getProduct() != null) {
                cpu.getProduct().setPhoto("http://localhost:8080/cpus/" + id + "/photo");
            }
            return ResponseEntity.ok(cpu);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public void deleteCPU(@PathVariable Long id) {
        cpuRepository.findById(id).ifPresent(cpu -> {
            Product product = cpu.getProduct();
            if (product != null) {
                String photoPath = product.getPhoto();
                if (photoPath != null && !photoPath.isEmpty()) {
                    String filePath = "C:\\Users\\pjzyw\\OneDrive\\Pulpit\\ztp\\backend\\src\\main\\resources\\public\\" + photoPath; 
                    File photoFile = new File(filePath);
                    if (photoFile.exists()) {
                        photoFile.delete();
                    }
                }
                cpu.setProduct(null);
                productRepository.delete(product);
            }
            cpuRepository.delete(cpu);
        });
    }

    @PutMapping("/{id}")
    public CPU updateCPU(@PathVariable Long id, @RequestBody CPU updatedCPU) {
        return cpuRepository.findById(id)
            .map(cpu -> {
                cpu.setSpeed(updatedCPU.getSpeed());
                cpu.setArchitecture(updatedCPU.getArchitecture());
                cpu.setSupportedMemory(updatedCPU.getSupportedMemory());
                cpu.setCooling(updatedCPU.isCooling());
                cpu.setThreads(updatedCPU.getThreads());
                cpu.setTechnologicalProcess(updatedCPU.getTechnologicalProcess());
                cpu.setPowerConsumption(updatedCPU.getPowerConsumption());

                Product product = cpu.getProduct();
                if (product != null) {
                    product.setManufacturer(updatedCPU.getProduct().getManufacturer());
                    product.setModel(updatedCPU.getProduct().getModel());
                    product.setPrice(updatedCPU.getProduct().getPrice());
                    product.setPhoto(cpu.getProduct().getPhoto());
                }

                return cpuRepository.save(cpu);
            })
            .orElseThrow(() -> new IllegalArgumentException("CPU not found with id " + id));
    }

    @PostMapping
    public CPU addCPU(@RequestBody CPU newCPU) {
        Product product = newCPU.getProduct();
        if (product != null) {
            product.setPhoto("");
            product = productRepository.save(product);
            newCPU.setProduct(product);
        }
        
        CPU savedCPU = cpuRepository.save(newCPU);
        return savedCPU;
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getCPUPhoto(@PathVariable Long id) {
        return (ResponseEntity<byte[]>) cpuRepository.findById(id)
            .map(cpu -> {
                Product product = cpu.getProduct();
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
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product photo not found for CPU with id: " + id);
                }
            })
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("CPU not found with id: " + id));
    }


    @DeleteMapping("/{id}/photo")
    public ResponseEntity<Object> deleteCPUPhoto(@PathVariable Long id) {
        return cpuRepository.findById(id)
            .map(cpu -> {
                Product product = cpu.getProduct();
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
    public ResponseEntity<Object> updateCPUPhoto(@PathVariable Long id, @RequestParam("photo") MultipartFile photoFile) {
        if (photoFile.isEmpty()) {
            return ResponseEntity.badRequest().body("No photo provided.");
        }

        return (ResponseEntity<Object>) cpuRepository.findById(id)
            .map(cpu -> {
                Product product = cpu.getProduct();
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
                    return ResponseEntity.ok("CPU photo updated successfully.");
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
