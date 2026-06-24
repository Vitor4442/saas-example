package com.vtr.saas.services.impl;

import com.vtr.saas.entities.Category;
import com.vtr.saas.mappers.CategoryMapper;
import com.vtr.saas.repositories.CategoryRepository;
import com.vtr.saas.requests.CategoryRequest;
import com.vtr.saas.responses.CategoryResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public void create(CategoryRequest request) {
        checkTfCategoryExistsByName(request.getName());

        final Category category = categoryMapper.toEntity(request);
        this.categoryRepository.save(category);
    }

    @Override
    public void update(String id, CategoryRequest request) {

        final Optional<Category> existngCategory = this.categoryRepository.findById(id);
        if (existngCategory.isEmpty()){
            log.debug("Category not found");
            throw new RuntimeException("Category not found");
        }

        final Category category = existngCategory.get();

        if (!category.getName().equalsIgnoreCase(request.getName())) {
            checkTfCategoryExistsByName(request.getName());
        }

        final Category updatedCategory = categoryMapper.toEntity(request);
        updatedCategory.setId(id);
        this.categoryRepository.save(updatedCategory);
    }

    @Override
    public List<CategoryResponse> findAll() {
        return this.categoryRepository.findAll()
                .stream()
                .map(this.categoryMapper::toResponse)
                .toList();
    }

    @Override
    public CategoryResponse findById(String id) {
        return this.categoryRepository.findById(id)
                .map(this.categoryMapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
    }

    @Override
    public void delete(String id) {
        final Category category = this.categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        this.categoryRepository.delete(category);
    }

    private void checkTfCategoryExistsByName(String name) {
        final Optional<Category> category = this.categoryRepository.findByNameIgnoreCase(name);
        if(category.isPresent()){
            log.debug("Category already exists");
            throw new RuntimeException("Category Already exists");
        }
    }
}
