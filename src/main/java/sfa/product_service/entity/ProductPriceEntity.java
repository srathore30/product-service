package sfa.product_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldDefaults;
import sfa.product_service.constant.Status;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Entity
@Table(name = "product_price_table")
public class ProductPriceEntity extends BaseEntity{
    Long productId;
    Double wareHousePrice;
    Double stockListPrice;
    Double retailerPrice;
    Status status;
    Double gst;
}
