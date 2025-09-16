package sfa.product_service.repo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import sfa.product_service.entity.ProductMasterEntity;
import sfa.product_service.entity.ProductPriceEntity;

import java.util.Optional;

@Repository
public interface ProductMasterRepo extends JpaRepository<ProductMasterEntity, Long> {
    Optional<ProductMasterEntity> findByProductCode(String productCode);

    @Query("SELECT p FROM ProductMasterEntity p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<ProductMasterEntity> findByName(@Param("name") String name, Pageable pageable);

    @Query("SELECT p FROM ProductMasterEntity p WHERE LOWER(p.sku) LIKE LOWER(CONCAT('%', :sku, '%'))")
    Page<ProductMasterEntity> findBySku(@Param("sku") String sku, Pageable pageable);

}
