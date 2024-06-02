package com.ztp.ishop.controller;

import com.ztp.ishop.entity.Basket;
import com.ztp.ishop.entity.Product;
import com.ztp.ishop.entity.User;
import com.ztp.ishop.repository.BasketRepository;
import com.ztp.ishop.repository.ProductRepository;
import com.ztp.ishop.services.UserService;

import jakarta.transaction.Transactional;

import com.ztp.ishop.dto.BasketDto;
import com.ztp.ishop.dto.UserDTO;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
public class BasketController {

    @Autowired
    private BasketRepository basketRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserService userService;

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    @GetMapping
    public ResponseEntity<List<Basket>> getMyBasket() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDto = (UserDTO) authentication.getPrincipal();

        List<Basket> myBaskets = basketRepository.findByUserId(userDto.getId());
        
        myBaskets.forEach(basket -> {
        Product product = basket.getProduct();
        String photoUrl = String.format("http://localhost:8080/products/%d/photo", 
                                        product.getId());
        product.setPhoto(photoUrl);
 
    });
        return new ResponseEntity<>(myBaskets, HttpStatus.OK);
    }

    @Transactional
    @DeleteMapping
    public ResponseEntity<String> clearBasketForCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDto = (UserDTO) authentication.getPrincipal();
        basketRepository.deleteByUserId(userDto.getId());
        
        return ResponseEntity.ok("Basket cleared successfully");
    }

    @Transactional
    @PostMapping
    public ResponseEntity<Basket> addProductToBasket(@RequestBody BasketDto basketDto) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDto = (UserDTO) authentication.getPrincipal();

        User user = userService.findById(userDto.getId());
        Product product = productRepository.findById(basketDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Sprawdź, czy produkt jest już w koszyku użytkownika
        Basket existingBasket = basketRepository.findByUserAndProduct(user, product);

        if (existingBasket != null) {
            // Jeśli produkt już istnieje w koszyku, zwiększ ilość o 1
            existingBasket.setQuantity(existingBasket.getQuantity() + 1);
            Basket updatedBasket = basketRepository.save(existingBasket);
            return new ResponseEntity<>(updatedBasket, HttpStatus.CREATED);
        } else {
            // Jeśli produkt nie istnieje w koszyku, dodaj nowy wpis z ilością 1
            Basket basket = new Basket();
            basket.setUser(user);
            basket.setProduct(product);
            basket.setQuantity(1);
            Basket savedBasket = basketRepository.save(basket);
            return new ResponseEntity<>(savedBasket, HttpStatus.CREATED);
        }
    }

    @Transactional
    @DeleteMapping("/{productId}")
    public ResponseEntity<String> removeProductFromBasket(@PathVariable Long productId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDto = (UserDTO) authentication.getPrincipal();

        User user = userService.findById(userDto.getId());
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Sprawdź, czy produkt jest w koszyku użytkownika
        Basket existingBasket = basketRepository.findByUserAndProduct(user, product);

        if (existingBasket != null) {
            // Jeśli ilość produktu w koszyku jest większa niż 1, zmniejsz ilość o 1
            if (existingBasket.getQuantity() > 1) {
                existingBasket.setQuantity(existingBasket.getQuantity() - 1);
                basketRepository.save(existingBasket);
            } else {
                // Jeśli ilość produktu w koszyku wynosi 1, usuń produkt z koszyka
                basketRepository.delete(existingBasket);
            }
            return ResponseEntity.ok("Product removed from basket");
        } else {
            return ResponseEntity.badRequest().body("Product not found in basket");
        }
    }

    @Transactional
    @DeleteMapping("/remove/{productId}")
    public ResponseEntity<String> removeProductFromBasketForUser(@PathVariable Long productId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDto = (UserDTO) authentication.getPrincipal();

        User user = userService.findById(userDto.getId());
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        // Sprawdź, czy produkt jest w koszyku użytkownika
        Basket existingBasket = basketRepository.findByUserAndProduct(user, product);

        if (existingBasket != null) {
            // Usuń produkt z koszyka
            basketRepository.delete(existingBasket);
            return ResponseEntity.ok("Product removed from basket");
        } else {
            return ResponseEntity.badRequest().body("Product not found in basket");
        }
    }
}