package sfa.product_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sfa.product_service.entity.ProductPriceEntity;

import java.util.Optional;

@Repository
public interface ProductPriceRepo extends JpaRepository<ProductPriceEntity, Long> {
    Optional<ProductPriceEntity> findByProductId(Long productId);
    @Query("SELECT p FROM ProductPriceEntity p WHERE p.productId = :productId AND (p.wareHousePrice = :price OR p.stockListPrice = :price OR p.retailerPrice = :price)")
    Optional<ProductPriceEntity> findByPriceFilters(@Param("productId") Long productId, @Param("price") Double price);

}
