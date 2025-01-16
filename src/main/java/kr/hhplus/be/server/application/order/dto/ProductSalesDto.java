package kr.hhplus.be.server.application.order.dto;

public record ProductSalesDto(
        Long productId,
        String productName,
        Long price,
        Integer stock,
        Integer soldQuantity
) {

}