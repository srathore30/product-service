package sfa.product_service.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import sfa.product_service.entity.ProductMasterEntity;

@Repository
public interface ProductMasterRepo extends JpaRepository<ProductMasterEntity, Long> {
}
