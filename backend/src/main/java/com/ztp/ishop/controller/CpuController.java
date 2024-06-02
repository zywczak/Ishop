package com.ztp.ishop.controller;

import com.ztp.ishop.entity.CPU;
import com.ztp.ishop.entity.Product;
import com.ztp.ishop.repository.CpuRepository;
import com.ztp.ishop.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
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
                cpu.getProduct().setPhoto("http://localhost:8080/products/" + cpu.getProduct().getId() + "/photo");
            }
        }
        return cpus;
    }

    @GetMapping("/{id}")
    public ResponseEntity<CPU> getCPU(@PathVariable Long id) {
        CPU cpu = cpuRepository.findById(id).orElse(null);
        if (cpu != null) {
            if (cpu.getProduct() != null) {
                cpu.getProduct().setPhoto("http://localhost:8080/products/" + cpu.getProduct().getId() + "/photo");
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
}
