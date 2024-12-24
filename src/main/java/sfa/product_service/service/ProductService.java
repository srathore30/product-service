package sfa.product_service.service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import sfa.product_service.constant.ApiErrorCodes;
import sfa.product_service.constant.Status;
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductService {
    private final ProductMasterRepo productMasterRepo;
    private final ProductPriceRepo productPriceRepo;
    Double price;
    @Value("${aws.access-key-id}")
    private String accessKeyId;
    @Value("${aws.secret-access-key}")
    private String secretAccessKey;
    @Value("${aws.bucket}")
    private String bucketName;
    @Autowired
    private AmazonS3 amazonS3;

    @Transactional
    public ProductCreateRes createProduct(ProductReq request) {
        log.info("Creating product: {}", request);
        log.info("Product map to entity");
        ProductMasterEntity productMasterEntity = mapToProductMasterEntity(request);
        log.info("Product price map to entity");
        ProductPriceEntity productPriceEntity = mapToProductPriceEntity(request);
        log.info("Product is ready to save on product master repo");
        ProductMasterEntity savedProduct = productMasterRepo.save(productMasterEntity);
        productPriceEntity.setProductId(savedProduct.getId());
//        log.info("Product image is uploaded");
//        productMasterEntity.setImageUrl(uploadBase64File(request.getImageUrl()));
        log.info("Product is ready to save on price repo");
        productPriceRepo.save(productPriceEntity);
        return new ProductCreateRes(savedProduct.getId(), savedProduct.getImageUrl(), "Product created successfully");
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
        return new ProductCreateRes(productPriceEntity.getProductId(), productMasterEntity.getImageUrl(), "Product update successfully");
    }

    private ProductMasterEntity mapToProductMasterEntity(ProductReq request) {
        ProductMasterEntity productMasterEntity = new ProductMasterEntity();
        productMasterEntity.setName(request.getName());
        productMasterEntity.setSku(request.getSku());
        productMasterEntity.setUnitMeasurement(request.getUnitOfMeasurement());
        productMasterEntity.setBundleSize(request.getBundleSize());
        productMasterEntity.setImageUrl(uploadBase64File(request.getImageUrl()));
        return productMasterEntity;
    }

    private void updateProductMasterFromReq(ProductUpdateReq request, ProductMasterEntity productMasterEntity) {
        productMasterEntity.setName(request.getName());
        productMasterEntity.setSku(request.getSku());
        productMasterEntity.setUnitMeasurement(request.getUnitOfMeasurement());
        productMasterEntity.setBundleSize(request.getBundleSize());
        productMasterEntity.setImageUrl(uploadBase64File(request.getImageUrl()));
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
        List<ProductRes> productResList = new ArrayList<>();
        productMasterEntities.getContent().forEach(productMasterEntity -> {
            if (productMasterEntity.getStatus() == Status.ACTIVE) {
                Optional<ProductPriceEntity> optionalProductPriceEntity = productPriceRepo.findByProductId(productMasterEntity.getId());
                if (optionalProductPriceEntity.get().getStatus() == Status.ACTIVE) {
                    ProductPriceEntity productPriceEntity = optionalProductPriceEntity.get();
                    ProductRes productRes = mapToProductRes(productPriceEntity, productMasterEntity);
                    productResList.add(productRes);
                }
            }
        });
        return new PaginatedResp<>(productMasterEntities.getTotalElements(), productMasterEntities.getTotalPages(), productMasterEntities.getNumber(), productResList);
    }

    public void deleteProduct(String type, Long id) {
        if (type.equalsIgnoreCase("price")) {
            ProductPriceEntity productPriceEntity = productPriceRepo.findById(id).orElseThrow(() -> new NoSuchElementFoundException(ApiErrorCodes.PRODUCT__PRICE_NOT_FOUND.getErrorCode(), ApiErrorCodes.PRODUCT__PRICE_NOT_FOUND.getErrorMessage()));
            productPriceEntity.setStatus(Status.INACTIVE);
            productPriceRepo.save(productPriceEntity);
        } else if (type.equalsIgnoreCase("master")) {
            ProductMasterEntity productMasterEntity = productMasterRepo.findById(id).orElseThrow(() -> new NoSuchElementFoundException(ApiErrorCodes.PRODUCT_NOT_FOUND.getErrorCode(), ApiErrorCodes.PRODUCT_NOT_FOUND.getErrorMessage()));
            productMasterEntity.setStatus(Status.INACTIVE);
            productMasterRepo.save(productMasterEntity);
        } else {
            throw new InvalidInputException(ApiErrorCodes.INVALID_INPUT.getErrorCode(), ApiErrorCodes.INVALID_INPUT.getErrorMessage());
        }
    }

    //Image Upload for product
    public String uploadBase64File(String base64Url) {
        log.info("Decoded base64 file");
        byte[] fileBytes = Base64.getDecoder().decode(base64Url);
        log.info("Created input stream");
        InputStream fileStream = new ByteArrayInputStream(fileBytes);
        log.info("Prepare key that save in Database");
        String keyPrefix = "AdminName";
        String keySuffix = ".jpg";
        String key = keyPrefix + UUID.randomUUID() + keySuffix;
        log.info("Created key: {}", key);
        log.info("Prepare Object Meta Data");
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(fileBytes.length);
        metadata.setContentType("image/jpeg");

        try {
            log.info("Start uploading file");
            amazonS3.putObject(bucketName, key, fileStream, metadata);
            log.info("File uploaded successfully");
        } catch (AmazonServiceException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AWS Service Error: " + e.getErrorMessage(), e);
        } catch (AmazonClientException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "AWS Client Error: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error while uploading base64 file", e);
        }

        return key;
    }

    public String getProSignedUrl(String filePath, HttpMethod httpMethod) {
        log.info("Pro signed url details");
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MINUTE, 10);
        return amazonS3.generatePresignedUrl(bucketName, filePath, calendar.getTime(), httpMethod).toString();
    }
}
