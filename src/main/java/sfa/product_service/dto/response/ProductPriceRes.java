package sfa.product_service.dto.response;


import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProductPriceRes {
    Float warehousePrice;
    Float stockListPrice;
    Float retailerPrice;
    Float gstPercentage;
}
