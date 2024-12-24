package sfa.product_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;
import sfa.product_service.constant.Status;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductRes {
    String name;
    String sku;
    String unitOfMeasurement;
    Long productId;
    ProductPriceRes productPriceRes;
    Double bundleSize;
    String imageUrl;
}
