package sfa.product_service.dto.response;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class PaginatedResp<T> {
    long totalElements;
    int totalPages;
    int page;
    List<T> content;
}
