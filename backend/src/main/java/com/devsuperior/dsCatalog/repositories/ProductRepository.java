package com.devsuperior.dsCatalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.devsuperior.dsCatalog.entities.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

}
