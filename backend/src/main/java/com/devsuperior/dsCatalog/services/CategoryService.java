package com.devsuperior.dsCatalog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dsCatalog.dto.CategoryDTO;
import com.devsuperior.dsCatalog.entities.Category;
import com.devsuperior.dsCatalog.repositories.CategoryRepository;
import com.devsuperior.dsCatalog.services.exceptions.DatabaseException;
import com.devsuperior.dsCatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<CategoryDTO> findAll(Pageable pageable) {
        Page<Category> list = categoryRepository.findAll(pageable);
        return list.map(x -> new CategoryDTO(x));
    }

    @Transactional(readOnly = true)
    public CategoryDTO findById(Long id) {
        Category cat = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Resource not Found!"));
        return new CategoryDTO(cat);
    }

    @Transactional
    public CategoryDTO insert(CategoryDTO dto) {
        Category entity = new Category();
        dtoToEntity(entity, dto);
        entity = categoryRepository.save(entity);
        return new CategoryDTO(entity);
    }

    @Transactional
    public CategoryDTO update(CategoryDTO dto, Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado!");
        }
        Category entity = categoryRepository.getReferenceById(id);
        dtoToEntity(entity, dto);
        entity = categoryRepository.save(entity);
        return new CategoryDTO(entity);
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado!");
        }
        try {
            categoryRepository.deleteById(id);

        } catch(DatabaseException e){
            throw new DatabaseException("Erro de Integridade Referencial!");
        }
    }

        private void dtoToEntity(Category entity, CategoryDTO dto) {
        entity.setName(dto.getName());
    }
}
