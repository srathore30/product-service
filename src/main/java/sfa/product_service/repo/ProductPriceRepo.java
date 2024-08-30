package sfa.product_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sfa.product_service.entity.ProductPriceEntity;

import java.util.Optional;

@Repository
public interface ProductPriceRepo extends JpaRepository<ProductPriceEntity, Long> {
    Optional<ProductPriceEntity> findByProductId(Long productId);
}
