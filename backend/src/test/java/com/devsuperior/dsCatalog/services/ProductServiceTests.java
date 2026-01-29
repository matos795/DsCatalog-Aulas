package com.devsuperior.dsCatalog.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.dsCatalog.dto.ProductDTO;
import com.devsuperior.dsCatalog.entities.Category;
import com.devsuperior.dsCatalog.entities.Product;
import com.devsuperior.dsCatalog.repositories.CategoryRepository;
import com.devsuperior.dsCatalog.repositories.ProductRepository;
import com.devsuperior.dsCatalog.services.exceptions.DatabaseException;
import com.devsuperior.dsCatalog.services.exceptions.ResourceNotFoundException;
import com.devsuperior.dsCatalog.tests.Factory;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long dependentId;
    private PageImpl<Product> page;
    private Product product;
    private Category category;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 2L;
        dependentId = 3L;
        product = Factory.createProduct();
        category = Factory.creaCategory();
        page = new PageImpl<>(List.of(product));
        productDTO = Factory.createProductDTO();

        Mockito.doNothing().when(productRepository).deleteById(existingId);
        Mockito.doThrow(DatabaseException.class).when(productRepository).deleteById(dependentId);

        Mockito.when(productRepository.existsById(existingId)).thenReturn(true);
        Mockito.when(productRepository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(productRepository.existsById(dependentId)).thenReturn(true);

        Mockito.when(productRepository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(productRepository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(productRepository.getReferenceById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        Mockito.when(categoryRepository.getReferenceById(existingId)).thenReturn(category);
        Mockito.when(categoryRepository.getReferenceById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        Mockito.when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.findById(dependentId)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());
    }

    @Test
    public void deleteShouldDoNothingWhenIdExist() {

        Assertions.assertDoesNotThrow(() -> {
            productService.delete(existingId);
        });

        Mockito.verify(productRepository, Mockito.times(1)).deleteById(existingId);
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(nonExistingId);
        });
    }

    @Test
    public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
        Assertions.assertThrows(DatabaseException.class, () -> {
            productService.delete(dependentId);
        });
    }

    @Test
    public void findAllShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<ProductDTO> result = productService.findAll(pageable);

        Assertions.assertNotNull(result);
        Mockito.verify(productRepository, Mockito.times(1)).findAll(pageable);
    }

    @Test
    public void findByIdShouldReturnProductDTOWhenIdExists() {
        ProductDTO dto = productService.findById(existingId);

        Assertions.assertNotNull(dto);
    }

    @Test
    public void findByIdShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.findById(nonExistingId);
        });
    }

    @Test
    public void updateShouldReturnProductDTOWhenIdExists() {
        ProductDTO dto = productService.update(productDTO, existingId);
        
        Assertions.assertNotNull(dto);
    }

    @Test
    public void updateShouldThrowResourceNotFoundExceptionWhenIdDoesNotExist() {
        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.update(productDTO, nonExistingId);
        });
    }
}
