package kr.hhplus.be.server.domain.product.service;

import kr.hhplus.be.server.common.exception.ErrorMessage;
import kr.hhplus.be.server.domain.product.entity.Product;
import kr.hhplus.be.server.domain.product.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("유효한 인자로 상품 생성을 요청하면 생성된 Product 객체를 반환한다")
    void createProduct_ShouldCreateAndReturnProduct_WhenValidInputsAreProvided() {
        // Arrange
        String name = "Laptop";
        Long price = 1500L;
        Integer stock = 10;
        Long categoryId = 1L;
        Product product = new Product(name, price, stock, categoryId);

        when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        Product createdProduct = productService.createProduct(name, price, stock, categoryId);

        // Assert
        assertThat(createdProduct).isNotNull();
        assertThat(createdProduct.getName()).isEqualTo(name);
        assertThat(createdProduct.getPrice()).isEqualTo(price);
        assertThat(createdProduct.getStock()).isEqualTo(stock);
        assertThat(createdProduct.getCategoryId()).isEqualTo(categoryId);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("존재하는 제품의 재고가 주문량보다 많으면 재고 감량에 성공한다.")
    void validateStockAndReduceQuantityWithLock_ShouldReduceStock_WhenProductExistsAndStockIsSufficient() {
        // Arrange
        Long productId = 1L;
        Integer quantity = 5;
        Product product = new Product("Laptop", 1500L, 10, 1L);

        when(productRepository.findByIdWithLock(productId)).thenReturn(Optional.of(product));
        when(productRepository.save(product)).thenReturn(product);

        // Act
        Product updatedProduct = productService.validateStockAndReduceQuantityWithLock(productId, quantity);

        // Assert
        assertThat(updatedProduct.getStock()).isEqualTo(5); // Initial stock 10 - 5 = 5
        verify(productRepository, times(1)).findByIdWithLock(productId);
        verify(productRepository, times(1)).save(product);
    }

    @Test
    @DisplayName("존재하지 않는 상품의 재고 감량을 요청하면 IllegalArgumentException을 던진다")
    void validateStockAndReduceQuantityWithLock_ShouldThrowException_WhenProductDoesNotExist() {
        // Arrange
        Long productId = 1L;
        Integer quantity = 5;

        when(productRepository.findByIdWithLock(productId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> productService.validateStockAndReduceQuantityWithLock(productId, quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage(ErrorMessage.PRODUCT_NOT_FOUND.getMessage());
        verify(productRepository, times(1)).findByIdWithLock(productId);
        verify(productRepository, never()).save(any(Product.class));
    }

    @Test
    @DisplayName("카테고리 아이디 없이 제품 목록을 조회하면 전체 제품 목록을 반환한다")
    void getProductList_ShouldReturnAllProducts_WhenCategoryIdIsNull() {
        // Arrange
        Pageable pageable = mock(Pageable.class);
        List<Product> products = Arrays.asList(
                new Product("Laptop", 1500L, 10, 1L),
                new Product("Tablet", 800L, 20, 2L)
        );
        Page<Product> productPage = new PageImpl<>(products);

        when(productRepository.findAll(pageable)).thenReturn(productPage);

        // Act
        Page<Product> result = productService.getProductList(null, pageable);

        // Assert
        assertThat(result.getContent()).hasSize(2);
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("카테고리 아이디를 포함해서 제품 목록을 조회하면 카테고리에 해당하는 제품 목록을 반환한다")
    void getProductList_ShouldReturnProductsByCategory_WhenCategoryIdIsNotNull() {
        // Arrange
        Long categoryId = 1L;
        Pageable pageable = mock(Pageable.class);
        List<Product> products = List.of(new Product("Laptop", 1500L, 10, categoryId));
        Page<Product> productPage = new PageImpl<>(products);

        when(productRepository.findAllByCategoryId(categoryId, pageable)).thenReturn(productPage);

        // Act
        Page<Product> result = productService.getProductList(categoryId, pageable);

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getCategoryId()).isEqualTo(categoryId);
        verify(productRepository, times(1)).findAllByCategoryId(categoryId, pageable);
    }

    @Test
    @DisplayName("제품 아이디 목록으로 제품 목록을 조회하면 아이디목록에 해당하는 제품 목록을 반환한다")
    void getProductsByIds_ShouldReturnProducts_WhenProductIdsAreProvided() {
        // Arrange
        List<Long> productIds = List.of(1L, 2L);
        List<Product> products = Arrays.asList(
                new Product("Laptop", 1500L, 10, 1L),
                new Product("Tablet", 800L, 20, 2L)
        );

        when(productRepository.findAllById(productIds)).thenReturn(products);

        // Act
        List<Product> result = productService.getProductsByIds(productIds);

        // Assert
        assertThat(result).hasSize(2);
        verify(productRepository, times(1)).findAllById(productIds);
    }
}
