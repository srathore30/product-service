package sfa.product_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductUpdateReq {
    String name;
    String sku;
    String unitOfMeasurement;
    Double warehousePrice;
    Double stockListPrice;
    Double retailerPrice;
    Double gstPercentage;
}
