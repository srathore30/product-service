package sfa.product_service.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductPriceRes {
    Double warehousePrice;
    Double stockListPrice;
    Double retailerPrice;
    Double gstPercentage;
}
