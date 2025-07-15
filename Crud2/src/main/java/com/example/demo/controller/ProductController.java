package com.example.demo.controller;

import com.example.demo.model.Product;
import com.example.demo.repository.ProductRepository; // Import the JpaRepository 
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList; // Used for converting Iterable to List 
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {
	private final ProductRepository productRepository; // Inject the JpaRepository

	@Autowired
	public ProductController(ProductRepository productRepository) {
		this.productRepository = productRepository;
	}

	// CREATE Product (POST /api/products)
	@PostMapping
	public ResponseEntity<Product> createProduct(@RequestBody Product product) {
		Product savedProduct = productRepository.save(product); // save() handles both create and update
		return new ResponseEntity<>(savedProduct, HttpStatus.CREATED);
	}

	// READ All Products (GET /api/products)
	@GetMapping
	public ResponseEntity<List<Product>> getAllProducts() {
		List<Product> products = new ArrayList<>();
		productRepository.findAll().forEach(products::add); // findAll() returns Iterable, convert to List
		return new ResponseEntity<>(products, HttpStatus.OK);
	}

	// READ Product by ID (GET /api/products/{id})
	@GetMapping("/{id}")
	public ResponseEntity<Product> getProductById(@PathVariable Long id) {
		Optional<Product> product = productRepository.findById(id); // findById() returns Optional
		return product.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
				.orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	// UPDATE Product (PUT /api/products/{id})
	@PutMapping("/{id}")
	public ResponseEntity<Product> updateProduct(@PathVariable Long id, @RequestBody Product productDetails) {
		Optional<Product> productOptional = productRepository.findById(id);

		if (productOptional.isPresent()) {
			Product existingProduct = productOptional.get();
			existingProduct.setName(productDetails.getName());
			existingProduct.setPrice(productDetails.getPrice());

			Product updatedProduct = productRepository.save(existingProduct);
			return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	// DELETE Product (DELETE /api/products/{id})
	@DeleteMapping("/{id}")
	public ResponseEntity<HttpStatus> deleteProduct(@PathVariable Long id) {
		if (productRepository.existsById(id)) {
			productRepository.deleteById(id);
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}

	// --- New Endpoints to Demonstrate Custom JPA Queries ---
	// Custom Query Endpoint: Derived method (findByNameContainingIgnoreCase)
	// GET /api/products/search/name?name=laptop
	@GetMapping("/search/name")
	public ResponseEntity<List<Product>> searchProductsByName(@RequestParam String name) {
		List<Product> products = productRepository.findByNameContainingIgnoreCase(name);
		return new ResponseEntity<>(products, HttpStatus.OK);
	}

	// Custom Query Endpoint: JPQL Query (findProductsByNameAndMinPrice)
	// GET /api/products/search/jpql?name=smart&minPrice=500
	@GetMapping("/search/jpql")
	public ResponseEntity<List<Product>> searchProductsByJpql(@RequestParam String name,
			@RequestParam double minPrice) {
		List<Product> products = productRepository.findProductsByNameAndMinPrice(name, minPrice);
		return new ResponseEntity<>(products, HttpStatus.OK);
	}
	// Custom Query Endpoint: Native SQL Query(findProductsByPriceLessThanNative)

	// GET /api/products/search/native?maxPrice=400
	@GetMapping("/search/native")
	public ResponseEntity<List<Product>> searchProductsByNative(@RequestParam double maxPrice) {
		List<Product> products = productRepository.findProductsByPriceLessThanNative(maxPrice);
		return new ResponseEntity<>(products, HttpStatus.OK);
	}
	// Custom DML Query Endpoint: Update Product Name(updateProductName)

	// PUT /api/products/update-name/{id}?newName=NewNameValue
	@PutMapping("/update-name/{id}")
	public ResponseEntity<String> updateProductName(@PathVariable Long id, @RequestParam String newName) {
		int updatedRows = productRepository.updateProductName(id, newName);
		if (updatedRows > 0) {
			return new ResponseEntity<>("Product name updated successfully for ID: " + id, HttpStatus.OK);
		} else {
			return new ResponseEntity<>("Product not found for ID: " + id, HttpStatus.NOT_FOUND);
		}
	}
}