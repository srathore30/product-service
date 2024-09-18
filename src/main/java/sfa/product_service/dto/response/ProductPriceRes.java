package sfa.product_service.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductPriceRes {
    float warehousePrice;
    float stockListPrice;
    float retailerPrice;
    float gstPercentage;
}
