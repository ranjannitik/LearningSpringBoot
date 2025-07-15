package com.example.demo.repository;

import com.example.demo.model.Product; // Import our Product entity 
import org.springframework.data.jpa.repository.JpaRepository; // Import JpaRepository 
import org.springframework.data.jpa.repository.Modifying; // For DML queries 
import org.springframework.data.jpa.repository.Query; // For custom queries 
import org.springframework.data.repository.query.Param; // For named parameters in queries 
import org.springframework.stereotype.Repository; // Optional, but good practice 
import org.springframework.transaction.annotation.Transactional; // For transactional DML queries 
import java.util.List; 
@Repository // Marks this interface as a Spring Data JPA repository 
public interface ProductRepository extends JpaRepository<Product, Long> { 
 // JpaRepository provides out-of-the-box implementations for: 
 // save(), findById(), findAll(), deleteById(), existsById(), count(), etc. 
 // It also includes methods for pagination and sorting. 
 // --- Custom Query Methods (Derived from Method Names) --- 
 // Spring Data JPA automatically generates SQL based on the method name. 
 List<Product> findByNameContainingIgnoreCase(String name); // SELECT * FROM PRODUCTS WHERE LOWER(NAME) LIKE LOWER('%name%') 
 List<Product> findByPriceGreaterThan(double price); // SELECT * FROM PRODUCTS WHERE PRICE > price 
 // --- Custom JPQL Query --- 
 // @Query allows you to write custom queries using JPQL (Java Persistence Query Language) 
 // JPQL operates on entities (Product p) not directly on tables/columns. 
 @Query("SELECT p FROM Product p WHERE p.name LIKE %:name% AND p.price > :minPrice") 
 List<Product> findProductsByNameAndMinPrice(@Param("name") String name, 
@Param("minPrice") double minPrice); 
 // --- Custom Native SQL Query --- 
 // @Query with nativeQuery = true allows you to write raw SQL. 
 // This is less portable but useful for database-specific features. 
 @Query(value = "SELECT * FROM PRODUCTS WHERE PRICE < ?1", 
nativeQuery = true) 
 List<Product> findProductsByPriceLessThanNative(double maxPrice); 
 // --- Custom DML Query (UPDATE) --- 
 // @Modifying is required for DML operations (UPDATE, DELETE) with @Query. 
 // @Transactional is crucial to ensure the operation is committed. 
 @Modifying 
 @Transactional 
 @Query("UPDATE Product p SET p.name = :newName WHERE p.id = :id") 
 int updateProductName(@Param("id") Long id, @Param("newName") String 
newName); 
} 