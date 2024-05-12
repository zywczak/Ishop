package com.ztp.ishop.controller;

import com.ztp.ishop.entity.Cooler;
import com.ztp.ishop.entity.Product;
import com.ztp.ishop.repository.CoolerRepository;
import com.ztp.ishop.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import org.springframework.web.bind.annotation.CrossOrigin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/coolers")
public class CoolerController {

    @Autowired
    private CoolerRepository coolerRepository;

    @Autowired
    private ProductRepository productRepository;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<Cooler>> getAllCoolers() {
        List<Cooler> coolers = coolerRepository.findAll();
        for (Cooler cooler : coolers) {
            if (cooler.getProduct() != null) {
                cooler.getProduct().setPhoto("http://localhost:8080/coolers/" + cooler.getId() + "/photo");
            }
        }
        return ResponseEntity.ok(coolers);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cooler> getCooler(@PathVariable Long id) {
        Cooler cooler = coolerRepository.findById(id).orElse(null);
        if (cooler != null) {
            if (cooler.getProduct() != null) {
                cooler.getProduct().setPhoto("http://localhost:8080/coolers/" + id + "/photo");
            }
            return ResponseEntity.ok(cooler);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public void deleteCooler(@PathVariable Long id) {
        coolerRepository.findById(id).ifPresent(cooler -> {
            Product product = cooler.getProduct();
            if (product != null) {
                String photoPath = product.getPhoto();
                if (photoPath != null && !photoPath.isEmpty()) {
                    String filePath = "C:\\Users\\pjzyw\\OneDrive\\Pulpit\\ztp\\backend\\src\\main\\resources\\public\\" + photoPath; 
                    File photoFile = new File(filePath);
                    if (photoFile.exists()) {
                        photoFile.delete();
                    }
                }
                cooler.setProduct(null);
                productRepository.delete(product);
            }
            coolerRepository.delete(cooler);
        });
    }

    @PutMapping("/{id}")
    public Cooler updateCooler(@PathVariable Long id, @RequestBody Cooler updatedCooler) {
        return coolerRepository.findById(id)
            .map(cooler -> {
                cooler.setType(updatedCooler.getType());
                cooler.setFanCount(updatedCooler.getFanCount());
                cooler.setFanSize(updatedCooler.getFanSize());
                cooler.setBacklight(updatedCooler.isBacklight());
                cooler.setMaterial(updatedCooler.getMaterial());
                cooler.setRadiatorSize(updatedCooler.getRadiatorSize());
                cooler.setCompatibility(updatedCooler.getCompatibility());

                Product product = cooler.getProduct();
                if (product != null) {
                    product.setManufacturer(updatedCooler.getProduct().getManufacturer());
                    product.setModel(updatedCooler.getProduct().getModel());
                    product.setPrice(updatedCooler.getProduct().getPrice());
                    product.setPhoto(cooler.getProduct().getPhoto());
                }

                return coolerRepository.save(cooler);
            })
            .orElseThrow(() -> new IllegalArgumentException("Cooler not found with id " + id));
    }

    @PostMapping
    public Cooler addCooler(@RequestBody Cooler newCooler) {
        Product product = newCooler.getProduct();
        if (product != null) {
            product.setPhoto("");
            product = productRepository.save(product);
            newCooler.setProduct(product);
        }
        
        Cooler savedCooler = coolerRepository.save(newCooler);
        return savedCooler;
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/{id}/photo")
    public ResponseEntity<byte[]> getCoolerPhoto(@PathVariable Long id) {
        return (ResponseEntity<byte[]>) coolerRepository.findById(id)
            .map(cooler -> {
                Product product = cooler.getProduct();
                if (product != null) {
                    try {
                        String fileName;
                        if(product.getPhoto() == ""){
                            fileName = "brakfoto.png";
                        }else{
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
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Product photo not found for cooler with id: " + id);
                }
            })
            .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).body("Cooler not found with id: " + id));
    }


    @DeleteMapping("/{id}/photo")
    public ResponseEntity<Object> deleteCoolerPhoto(@PathVariable Long id) {
        return coolerRepository.findById(id)
            .map(cooler -> {
                Product product = cooler.getProduct();
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
    public ResponseEntity<Object> updateCoolerPhoto(@PathVariable Long id, @RequestParam("photo") MultipartFile photoFile) {
        if (photoFile.isEmpty()) {
            return ResponseEntity.badRequest().body("No photo provided.");
        }

        return (ResponseEntity<Object>) coolerRepository.findById(id)
            .map(cooler -> {
                Product product = cooler.getProduct();
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
                    return ResponseEntity.ok("Cooler photo updated successfully.");
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
