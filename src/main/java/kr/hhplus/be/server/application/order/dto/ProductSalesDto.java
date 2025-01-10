package kr.hhplus.be.server.application.order.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
public class ProductSalesDto {
    private final Long productId;
    private final String productName;
    private final Long price;
    private final Integer stock;
    private final Integer soldQuantity;
}