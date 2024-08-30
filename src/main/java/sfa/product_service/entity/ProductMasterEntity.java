package sfa.product_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import sfa.product_service.constant.OrderStatus;
import sfa.product_service.constant.SalesLevelConstant;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "product_master_table")
public class ProductMasterEntity extends BaseEntity{
    String name;
    String sku;
    String unitMeasurement;
}
