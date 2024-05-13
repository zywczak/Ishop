package com.ztp.ishop;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
public class IshopApplication {
	public static void main(String[] args) {
		SpringApplication.run(IshopApplication.class, args);
	}

	@RestController
    public static class TestController {

        @CrossOrigin(origins = "http://localhost:3000")
        @GetMapping("/api/test")
        public String testBackend() {
            return "ololololol dzłLa !";
        }
        @GetMapping("/")
        public String index() {
            return "Strona główna aplikacji!";
        }
    }
}
