package sfa.product_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sfa.product_service.entity.ProductMasterEntity;
import sfa.product_service.entity.ProductPriceEntity;

import java.util.Optional;

@Repository
public interface ProductMasterRepo extends JpaRepository<ProductMasterEntity, Long> {
    Optional<ProductMasterEntity> findByProductCode(String productCode);

}
