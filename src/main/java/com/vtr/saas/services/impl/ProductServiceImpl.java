package com.vtr.saas.services.impl;

import com.vtr.saas.common.PageResponse;
import com.vtr.saas.entities.Category;
import com.vtr.saas.entities.Product;
import com.vtr.saas.mappers.ProductMapper;
import com.vtr.saas.repositories.CategoryRepository;
import com.vtr.saas.repositories.ProductRepository;
import com.vtr.saas.requests.ProductRequest;
import com.vtr.saas.responses.ProductResponse;
import com.vtr.saas.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public void create(final ProductRequest productRequest){
        checkIfProductAlreadyExistsByReference(productRequest.getReference());
        checkIfCategoryExistById(productRequest.getCategoryId());
        final Product entity = this.productMapper.toEntity(productRequest);
        this.productRepository.save(entity);
    }

    @Override
    public void update(final String id, final ProductRequest request) {
        final Optional<Product> productExists = this.productRepository.findById(id);
        if (productExists.isEmpty()) {
            log.debug("Product does not exist");
            throw new EntityNotFoundException("Product does not exist");
        }

        if (!productExists.get().getReference().equalsIgnoreCase(request.getReference())) {
            checkIfProductAlreadyExistsByReference(request.getReference());
        }

        checkIfCategoryExistById(request.getCategoryId());

        final Product productToUpdate = this.productMapper.toEntity(request);
        productToUpdate.setId(id);
        this.productRepository.save(productToUpdate);

    }

    @Override
    public PageResponse<ProductResponse> findAll(int page, int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<Product> products = this.productRepository.findAll(pageRequest);
        final Page<ProductResponse> productsResponses = products.map(this.productMapper::toResponse);
        return PageResponse.of(productsResponses);
    }

    @Override
    public ProductResponse findById(String id) {
        return this.productRepository.findById(id)
                .map(this.productMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Product does not exist"));
    }

    @Override
    public void delete(String id) {
        final Product product = this.productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Product does not exist"));
        this.productRepository.delete(product);
    }


    private void checkIfCategoryExistById( final String categoryId ) {
        final Optional<Category> category = categoryRepository.findById(categoryId);
        if(category.isEmpty()) {
            log.debug("Category does not exist");
            throw new RuntimeException("Category does not exist");
        }
    }

    private void checkIfProductAlreadyExistsByReference(final String refence){
        final Optional<Product> product = productRepository.findByReferenceIgoneCase(refence);
            if (product.isPresent()){
                log.debug("Product Already existing");
                throw new RuntimeException("Product Already exists");
            }
    }

}
