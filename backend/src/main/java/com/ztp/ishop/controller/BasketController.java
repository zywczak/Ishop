package com.ztp.ishop.controller;

import com.ztp.ishop.entity.Basket;
import com.ztp.ishop.repository.BasketRepository;
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

    @Value("${security.jwt.token.secret-key:secret-key}")
    private String secretKey;

    @GetMapping
    public ResponseEntity<List<Basket>> getMyBasket() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDTO userDto = (UserDTO) authentication.getPrincipal();

        List<Basket> myBaskets = basketRepository.findByUserId(userDto.getId());
        
        return new ResponseEntity<>(myBaskets, HttpStatus.OK);
    }



//     @PostMapping("/{product_id}/add")
//     public ResponseEntity<?> addProductToBasket(@PathVariable("product_id") Long productId) {
//         try {
//             Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//             String jwtToken = (String) authentication.getCredentials();
//             Algorithm algorithm = Algorithm.HMAC256(secretKey);
//             DecodedJWT decodedJWT = JWT.require(algorithm).build().verify(jwtToken);
//             String userEmail = decodedJWT.getSubject();
//             UserDTO user = userAuthenticationProvider.getUserService().findByEmail(userEmail);

//             if (user == null) {
//                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//             }

//             Basket basket = basketRepository.findByUserId(user.getId());

//             if (basket == null) {
//                 return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User's basket not found");
//             }

//             Optional<Product> productOptional = basket.getProducts().stream()
//                     .filter(product -> product.getId().equals(productId))
//                     .findFirst();

//             if (productOptional.isPresent()) {
//                 // If product is already in the basket, increase its quantity by 1
//                 Product product = productOptional.get();
//                 product.setQuantity(product.getQuantity() + 1);
//             } else {
//                 // If product is not in the basket, add it with quantity 1
//                 Product product = new Product(); // Assuming you have a Product class
//                 product.setId(productId);
//                 product.setQuantity(1);
//                 basket.getProducts().add(product);
//             }

//             basketRepository.save(basket);

//             return ResponseEntity.ok(basket);
//         } catch (Exception e) {
//             return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized access");
//         }
//     }
}
