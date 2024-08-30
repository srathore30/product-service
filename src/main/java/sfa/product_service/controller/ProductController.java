package sfa.product_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sfa.product_service.dto.request.ProductReq;
import sfa.product_service.dto.request.ProductUpdateReq;
import sfa.product_service.dto.response.ProductCreateRes;
import sfa.product_service.dto.response.ProductRes;
import sfa.product_service.service.ProductService;


@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductCreateRes> createProduct(@RequestBody ProductReq productReq) {
        ProductCreateRes productCreateRes = productService.createProduct(productReq);
        return new ResponseEntity<>(productCreateRes, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductRes> getProductById(@PathVariable Long id) {
        ProductRes productRes = productService.getProductById(id);
        return new ResponseEntity<>(productRes, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductCreateRes> updateProduct(@PathVariable Long id, @RequestBody ProductUpdateReq productUpdateReq) {
        ProductCreateRes productRes = productService.updateProduct(id, productUpdateReq);
        return new ResponseEntity<>(productRes, HttpStatus.CREATED);
    }
}
