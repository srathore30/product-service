package sfa.product_service.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import sfa.product_service.constant.ApiErrorCodes;
import sfa.product_service.dto.request.ProductReq;
import sfa.product_service.dto.request.ProductUpdateReq;
import sfa.product_service.dto.response.PaginatedResp;
import sfa.product_service.dto.response.ProductCreateRes;
import sfa.product_service.dto.response.ProductPriceRes;
import sfa.product_service.dto.response.ProductRes;
import sfa.product_service.entity.ProductMasterEntity;
import sfa.product_service.entity.ProductPriceEntity;
import sfa.product_service.exception.InvalidInputException;
import sfa.product_service.exception.NoSuchElementFoundException;
import sfa.product_service.repo.ProductMasterRepo;
import sfa.product_service.repo.ProductPriceRepo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductMasterRepo productMasterRepo;
    private final ProductPriceRepo productPriceRepo;
    Double price;

    @Transactional
    public ProductCreateRes createProduct(ProductReq request) {
        log.info("Creating product: {}", request);
        ProductMasterEntity productMasterEntity = mapToProductMasterEntity(request);
        ProductPriceEntity productPriceEntity = mapToProductPriceEntity(request);
        ProductMasterEntity savedProduct = productMasterRepo.save(productMasterEntity);
        productPriceEntity.setProductId(savedProduct.getId());
        productPriceRepo.save(productPriceEntity);
        return new ProductCreateRes(savedProduct.getId(), "Product created successfully");
    }

    public ProductRes getProductById(Long id) {
        Optional<ProductMasterEntity> optionalProductMasterEntity = productMasterRepo.findById(id);
        if (optionalProductMasterEntity.isEmpty()) {
            throw new NoSuchElementFoundException(ApiErrorCodes.PRODUCT_NOT_FOUND.getErrorCode(), ApiErrorCodes.PRODUCT_NOT_FOUND.getErrorMessage());
        }
        Optional<ProductPriceEntity> optionalProductPriceEntity = productPriceRepo.findByProductId(optionalProductMasterEntity.get().getId());
        if (optionalProductPriceEntity.isEmpty()) {
            throw new NoSuchElementFoundException(ApiErrorCodes.PRODUCT__PRICE_NOT_FOUND.getErrorCode(), ApiErrorCodes.PRODUCT__PRICE_NOT_FOUND.getErrorMessage());
        }
        return mapToProductRes(optionalProductPriceEntity.get(), optionalProductMasterEntity.get());
    }

    @Transactional
    public ProductCreateRes updateProduct(Long id, ProductUpdateReq request) {
        Optional<ProductMasterEntity> optionalProductMasterEntity = productMasterRepo.findById(id);
        if (optionalProductMasterEntity.isEmpty()) {
            throw new NoSuchElementFoundException(ApiErrorCodes.PRODUCT_NOT_FOUND.getErrorCode(), ApiErrorCodes.PRODUCT_NOT_FOUND.getErrorMessage());
        }
        Optional<ProductPriceEntity> optionalProductPriceEntity = productPriceRepo.findByProductId(optionalProductMasterEntity.get().getId());
        if (optionalProductPriceEntity.isEmpty()) {
            throw new NoSuchElementFoundException(ApiErrorCodes.PRODUCT__PRICE_NOT_FOUND.getErrorCode(), ApiErrorCodes.PRODUCT__PRICE_NOT_FOUND.getErrorMessage());
        }
        ProductMasterEntity productMasterEntity = optionalProductMasterEntity.get();
        ProductPriceEntity productPriceEntity = optionalProductPriceEntity.get();
        updateProductMasterFromReq(request, productMasterEntity);
        updateProductPriceFromReq(request, productPriceEntity);
        productMasterRepo.save(productMasterEntity);
        productPriceRepo.save(productPriceEntity);
        return new ProductCreateRes(productPriceEntity.getProductId(), "Product update successfully");
    }

    private ProductMasterEntity mapToProductMasterEntity(ProductReq request) {
        ProductMasterEntity productMasterEntity = new ProductMasterEntity();
        productMasterEntity.setName(request.getName());
        productMasterEntity.setSku(request.getSku());
        productMasterEntity.setUnitMeasurement(request.getUnitOfMeasurement());
        return productMasterEntity;
    }

    private void updateProductMasterFromReq(ProductUpdateReq request, ProductMasterEntity productMasterEntity) {
        productMasterEntity.setName(request.getName());
        productMasterEntity.setSku(request.getSku());
        productMasterEntity.setUnitMeasurement(request.getUnitOfMeasurement());
    }

    private void updateProductPriceFromReq(ProductUpdateReq request, ProductPriceEntity productPriceEntity) {
        productPriceEntity.setWareHousePrice(request.getWarehousePrice());
        productPriceEntity.setStockListPrice(request.getStockListPrice());
        productPriceEntity.setGst(request.getGstPercentage());
        productPriceEntity.setRetailerPrice(request.getRetailerPrice());
    }

    private ProductPriceEntity mapToProductPriceEntity(ProductReq request) {
        ProductPriceEntity productPriceEntity = new ProductPriceEntity();
        productPriceEntity.setWareHousePrice(request.getWarehousePrice());
        productPriceEntity.setStockListPrice(request.getStockListPrice());
        productPriceEntity.setGst(request.getGstPercentage());
        productPriceEntity.setRetailerPrice(request.getRetailerPrice());
        return productPriceEntity;
    }

    private ProductRes mapToProductRes(ProductPriceEntity productPrice, ProductMasterEntity productMaster) {
        ProductRes productRes = new ProductRes();
        productRes.setName(productMaster.getName());
        productRes.setProductId(productMaster.getId());
        productRes.setSku(productMaster.getSku());
        productRes.setUnitOfMeasurement(productMaster.getUnitMeasurement());
        productRes.setProductPriceRes(mapToProductPriceRes(productPrice));
        return productRes;
    }

    private ProductPriceRes mapToProductPriceRes(ProductPriceEntity productPrice) {
        ProductPriceRes productPriceRes = new ProductPriceRes();
        productPriceRes.setRetailerPrice(productPrice.getRetailerPrice());
        productPriceRes.setWarehousePrice(productPrice.getWareHousePrice());
        productPriceRes.setStockListPrice(productPrice.getStockListPrice());
        productPriceRes.setGstPercentage(productPrice.getGst());
        return productPriceRes;
    }

    public Double getProductPriceByIdAndType(Long id, String priceType) {
        Optional<ProductMasterEntity> optionalProductMasterEntity = productMasterRepo.findById(id);
        if (optionalProductMasterEntity.isEmpty()) {
            throw new NoSuchElementFoundException(ApiErrorCodes.PRODUCT_NOT_FOUND.getErrorCode(), ApiErrorCodes.PRODUCT_NOT_FOUND.getErrorMessage());
        }
        Optional<ProductPriceEntity> optionalProductPriceEntity = productPriceRepo.findByProductId(optionalProductMasterEntity.get().getId());
        if (optionalProductPriceEntity.isEmpty()) {
            throw new NoSuchElementFoundException(ApiErrorCodes.PRODUCT__PRICE_NOT_FOUND.getErrorCode(), ApiErrorCodes.PRODUCT__PRICE_NOT_FOUND.getErrorMessage());
        }
        ProductPriceEntity productPriceEntity = optionalProductPriceEntity.get();
        return switch (priceType.toLowerCase()) {
            case "warehouse" -> productPriceEntity.getWareHousePrice();
            case "stocklist" -> productPriceEntity.getStockListPrice();
            case "retailer" -> productPriceEntity.getRetailerPrice();
            case "gst" -> productPriceEntity.getGst();
            default ->
                    throw new InvalidInputException(ApiErrorCodes.INVALID_INPUT.getErrorCode(), ApiErrorCodes.INVALID_INPUT.getErrorMessage());
        };
    }

    public PaginatedResp<ProductRes> getAllProduct(int page, int pageSize, String sortBy, String sortDirection) {
        Sort sort = sortDirection.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, pageSize, sort);
        Page<ProductMasterEntity> productMasterEntities = productMasterRepo.findAll(pageable);
        List<ProductRes> productResList=new ArrayList<>();
        productMasterEntities.getContent().forEach(productMasterEntity -> {
            Optional<ProductPriceEntity> optionalProductPriceEntity = productPriceRepo.findByProductId(productMasterEntity.getId());
            if (optionalProductPriceEntity.isEmpty()) {
                throw new NoSuchElementFoundException(ApiErrorCodes.PRODUCT__PRICE_NOT_FOUND.getErrorCode(), ApiErrorCodes.PRODUCT__PRICE_NOT_FOUND.getErrorMessage());
            }
            ProductPriceEntity productPriceEntity = optionalProductPriceEntity.get();
            ProductRes productRes = mapToProductRes(productPriceEntity, productMasterEntity);
          productResList.add(productRes);
        });
        return new PaginatedResp<>(productMasterEntities.getTotalElements(), productMasterEntities.getTotalPages(), productMasterEntities.getNumber(), productResList);
    }

    public void deleteProduct(String type,Long id) {
        if (type.equalsIgnoreCase("price")) {
            productPriceRepo.findById(id).orElseThrow(() -> new NoSuchElementFoundException(ApiErrorCodes.PRODUCT__PRICE_NOT_FOUND.getErrorCode(), ApiErrorCodes.PRODUCT__PRICE_NOT_FOUND.getErrorMessage()));
            productPriceRepo.deleteById(id);
        } else if (type.equalsIgnoreCase("master")) {
            productMasterRepo.findById(id).orElseThrow(() -> new NoSuchElementFoundException(ApiErrorCodes.PRODUCT_NOT_FOUND.getErrorCode(), ApiErrorCodes.PRODUCT_NOT_FOUND.getErrorMessage()));
            productMasterRepo.deleteById(id);
        }else {
            throw new InvalidInputException(ApiErrorCodes.INVALID_INPUT.getErrorCode(), ApiErrorCodes.INVALID_INPUT.getErrorMessage());
        }
    }
}
