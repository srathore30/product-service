package sfa.product_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "product_price_table")
public class ProductPriceEntity extends BaseEntity{
    Long productId;
    float wareHousePrice;
    float stockListPrice;
    float retailerPrice;
    float gst;
}
