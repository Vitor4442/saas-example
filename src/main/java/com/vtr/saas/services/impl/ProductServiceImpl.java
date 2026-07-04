package com.vtr.saas.services.impl;

import com.vtr.saas.entities.Category;
import com.vtr.saas.entities.Product;
import com.vtr.saas.mappers.CategoryMapper;
import com.vtr.saas.mappers.ProductMapper;
import com.vtr.saas.repositories.CategoryRepository;
import com.vtr.saas.repositories.ProductRepository;
import com.vtr.saas.requests.ProductRequest;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    public void create (final ProductRequest productRequest){
        checkIfProductExists(productRequest.getReference());
        checkIfCategoryExists(productRequest.getCategoryId());
        final Product entity = this.productMapper.toEntity(productRequest);
        this.productRepository.save(entity);
    }

    private void checkIfCategoryExists( final String categoryId ) {
        final Optional<Category> category = categoryRepository.findById(categoryId);
        if(category.isEmpty()) {
            log.debug("Category does not exist");
            throw new RuntimeException("Category does not exist");
        }
    }

    private void checkIfProductExists(final String refence){
        final Optional<Product> product = productRepository.findByReferenceIgoneCase(refence);
            if (product.isPresent()){
                log.debug("Product Already existing");
                throw new RuntimeException("Product Already exists");
            }
    }

}
