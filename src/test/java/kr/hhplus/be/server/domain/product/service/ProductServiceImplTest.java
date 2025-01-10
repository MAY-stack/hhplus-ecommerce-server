package kr.hhplus.be.server.domain.product.service;

import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.exception.ProductNotFoundException;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void 제품을_생성할_수_있다() {
        // given
        Product mockProduct = new Product("상품 A", 10000L, 10, 1L);
        doReturn(mockProduct).when(productRepository).save(any(Product.class));

        // when
        Product createdProduct = productService.createProduct("상품 A", 10000L, 10, 1L);

        // then
        ArgumentCaptor<Product> captor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(captor.capture());
        Product capturedProduct = captor.getValue();

        assertEquals("상품 A", capturedProduct.getName());
        assertEquals(10000L, capturedProduct.getPrice());
        assertEquals(10, capturedProduct.getStock());
        assertEquals(1L, capturedProduct.getCategoryId());

        assertEquals(mockProduct, createdProduct); // 반환된 값도 확인
    }


    @Test
    void 제품_재고를_감소시킬_수_있다() {
        // given
        Product product = new Product("상품 B", 15000L, 5, 2L);
        when(productRepository.findByIdWithLock(1L)).thenReturn(Optional.of(product));

        // when
        productService.validateStockAndReduceQuantityWithLock(1L, 3);

        // then
        assertEquals(2, product.getStock());
        verify(productRepository, times(1)).findByIdWithLock(1L);
    }

    @Test
    void 존재하지_않는_제품을_조회하면_예외가_발생한다() {
        // given
        when(productRepository.findByIdWithLock(1L)).thenReturn(Optional.empty());

        // when & then
        assertThrows(ProductNotFoundException.class, () ->
                productService.validateStockAndReduceQuantityWithLock(1L, 3));
    }

    @Test
    void 특정_카테고리의_제품을_조회할_수_있다() {
        // given
        List<Product> products = Arrays.asList(
                new Product("상품 A", 10000L, 10, 1L),
                new Product("상품 B", 15000L, 5, 1L)
        );
        when(productRepository.findAllByCategoryId(1L)).thenReturn(products);

        // when
        List<Product> productList = productService.getProductList(1L);

        // then
        assertEquals(2, productList.size());
        verify(productRepository, times(1)).findAllByCategoryId(1L);
    }
}
