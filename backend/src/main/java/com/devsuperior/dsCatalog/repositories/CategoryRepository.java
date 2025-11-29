package com.devsuperior.dsCatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devsuperior.dsCatalog.entities.Category;

public interface CategoryRepository extends JpaRepository<Category, Long>{

}
