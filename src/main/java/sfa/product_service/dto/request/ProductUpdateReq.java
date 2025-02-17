package sfa.product_service.dto.request;

import lombok.*;
import lombok.experimental.FieldDefaults;
import sfa.product_service.constant.Status;

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
    String productCode;
    Double stockListPrice;
    Double retailerPrice;
    Double gstPercentage;
    Double bundleSize;
    String imageUrl;
    Status status;
}
