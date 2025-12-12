package com.devsuperior.dsCatalog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dsCatalog.dto.CategoryDTO;
import com.devsuperior.dsCatalog.dto.ProductDTO;
import com.devsuperior.dsCatalog.entities.Category;
import com.devsuperior.dsCatalog.entities.Product;
import com.devsuperior.dsCatalog.repositories.ProductRepository;
import com.devsuperior.dsCatalog.services.exceptions.DatabaseException;
import com.devsuperior.dsCatalog.services.exceptions.ResourceNotFoundException;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public Page<ProductDTO> findAll(Pageable pageable) {
        Page<Product> list = productRepository.findAll(pageable);
        return list.map(x -> new ProductDTO(x));
    }

    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Product cat = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not Found!"));
        return new ProductDTO(cat);
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product entity = new Product();
        dtoToEntity(entity, dto);
        entity = productRepository.save(entity);
        return new ProductDTO(entity);
    }

    @Transactional
    public ProductDTO update(ProductDTO dto, Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado!");
        }
        Product entity = productRepository.getReferenceById(id);
        dtoToEntity(entity, dto);
        entity = productRepository.save(entity);
        return new ProductDTO(entity);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado!");
        }
        try {
            productRepository.deleteById(id);

        } catch(DatabaseException e){
            throw new DatabaseException("Erro de Integridade Referencial!");
        }
    }

        private void dtoToEntity(Product entity, ProductDTO dto) {
        entity.setName(dto.getName());
        entity.setDescription(dto.getDescription());
        entity.setPrice(dto.getPrice());
        entity.setDate(dto.getDate());
        entity.setImgUrl(dto.getImgUrl());

        entity.getCategories().clear();
        for (CategoryDTO cat : dto.getCategories()) {
            Category c = new Category(cat.getId(), cat.getName());
            entity.getCategories().add(c);
        }
    }
}
