package com.mcq.productsms.controller;

import com.mcq.productsms.dto.ProductDto;
import com.mcq.productsms.exception.ErrorMessage;
import com.mcq.productsms.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/products")
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<String> createProduct(@RequestBody ProductDto productDto) {
        String productId = productService.createProduct(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(productId);
    }

    @PostMapping("/sync")
    public ResponseEntity<?> createProductSynchronous(@RequestBody ProductDto productDto) {
        String productId;
        try {
            productId = productService.createProductSynchronous(productDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorMessage(new Date(), e.getMessage(), "/products"));
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(productId);
    }
}
