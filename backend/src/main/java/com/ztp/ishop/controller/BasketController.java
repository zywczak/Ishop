package com.ztp.ishop.controller;

import com.ztp.ishop.entity.Basket;
import com.ztp.ishop.repository.BasketRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequestMapping("/baskets")
public class BasketController {

    @Autowired
    private BasketRepository basketRepository;

    @GetMapping("/{userId}")
    public List<Basket> getBasketsByUserId(@PathVariable Long userId) {
        return basketRepository.findByUserId(userId);
    }

    @PostMapping
    public Basket addBasket(@RequestBody Basket basket) {
        return basketRepository.save(basket);
    }

    @PutMapping("/{id}")
    public Basket updateBasket(@PathVariable Long id, @RequestBody Basket updatedBasket) {
        return basketRepository.findById(id)
                .map(basket -> {
                    basket.setUser(updatedBasket.getUser());
                    basket.setProduct(updatedBasket.getProduct());
                    basket.setQuantity(updatedBasket.getQuantity());
                    return basketRepository.save(basket);
                })
                .orElseThrow(() -> new IllegalArgumentException("Basket not found with id " + id));
    }

    @DeleteMapping("/{userId}")
    public void deleteBasketByUserId(@PathVariable Long userId) {
        basketRepository.deleteByUserId(userId);
    }
}
