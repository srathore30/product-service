package sfa.product_service.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sfa.product_service.AuthUtils.JwtHelper;
import sfa.product_service.constant.UserRole;
import sfa.product_service.dto.request.ProductReq;
import sfa.product_service.dto.request.ProductUpdateReq;
import sfa.product_service.dto.response.PaginatedResp;
import sfa.product_service.dto.response.ProductCreateRes;
import sfa.product_service.dto.response.ProductRes;
import sfa.product_service.interceptor.UserAuthorization;
import sfa.product_service.service.ProductService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {
    private final ProductService productService;

    @PostMapping
    @UserAuthorization(allowedRoles = {UserRole.Create_Manager, UserRole.Edit_Manager, UserRole.Delete_Manager,UserRole.View_Manager,UserRole.Manager})
    public ResponseEntity<ProductCreateRes> createProduct(@RequestBody ProductReq productReq) {
        ProductCreateRes productCreateRes = productService.createProduct(productReq);
        return new ResponseEntity<>(productCreateRes, HttpStatus.CREATED);
    }
    @GetMapping("/{id}")
    @UserAuthorization(allowedRoles = {UserRole.Create_Manager, UserRole.Edit_Manager, UserRole.Delete_Manager,UserRole.View_Manager,UserRole.Manager})
    public ResponseEntity<ProductRes> getProductById(@PathVariable Long id) {
        ProductRes productRes = productService.getProductById(id);
        return new ResponseEntity<>(productRes, HttpStatus.OK);
    }

    @GetMapping("/getByIdAndPriceType/{id}")
    @UserAuthorization(allowedRoles = {UserRole.Create_Manager, UserRole.Edit_Manager, UserRole.Delete_Manager,UserRole.View_Manager,UserRole.Manager})
    public ResponseEntity<Double> getProductByIdAndPriceType(@PathVariable Long id, @RequestParam String priceType) {
        return new ResponseEntity<>(productService.getProductPriceByIdAndType(id, priceType), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @UserAuthorization(allowedRoles = {UserRole.Create_Manager, UserRole.Edit_Manager, UserRole.Delete_Manager,UserRole.View_Manager,UserRole.Manager})
    public ResponseEntity<ProductCreateRes> updateProduct(@PathVariable Long id, @RequestBody ProductUpdateReq productUpdateReq) {
        ProductCreateRes productRes = productService.updateProduct(id, productUpdateReq);
        return new ResponseEntity<>(productRes, HttpStatus.OK);
    }

    @GetMapping("/all")
    @UserAuthorization(allowedRoles = {UserRole.Create_Manager, UserRole.Edit_Manager, UserRole.Delete_Manager,UserRole.View_Manager,UserRole.Manager})
    public ResponseEntity<PaginatedResp<ProductRes>> getAllProduct(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int pageSize, @RequestParam(defaultValue = "createdDate") String sortBy, @RequestParam(defaultValue = "desc") String sortDirection) {
        return new ResponseEntity<>(productService.getAllProduct(page, pageSize, sortBy, sortDirection), HttpStatus.OK);
    }
    @DeleteMapping("/{id}")
    @UserAuthorization(allowedRoles = {UserRole.Create_Manager, UserRole.Edit_Manager, UserRole.Delete_Manager,UserRole.View_Manager,UserRole.Manager})
    public ResponseEntity<String> deleteProduct(@RequestParam String type,@PathVariable Long id) {
        productService.deleteProduct(type,id);
        return new ResponseEntity<>("Product deleted successfully", HttpStatus.OK);
    }
}