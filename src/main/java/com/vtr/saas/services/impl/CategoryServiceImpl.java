package com.vtr.saas.services.impl;

import com.vtr.saas.requests.CategoryRequest;
import com.vtr.saas.responses.CategoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    @Override
    public void create(CategoryRequest request) {

    }

    @Override
    public void update(String id, CategoryRequest request) {

    }

    @Override
    public CategoryResponse findById(String id) {
        return null;
    }

    @Override
    public void delete(String id) {

    }
}
