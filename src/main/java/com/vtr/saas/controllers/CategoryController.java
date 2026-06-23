package com.vtr.saas.controllers;

import com.vtr.saas.requests.CategoryRequest;
import com.vtr.saas.responses.CategoryResponse;
import com.vtr.saas.services.impl.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService service;

    @PostMapping
    private ResponseEntity<Void> createCategory(@Valid @RequestBody final CategoryRequest request){
        this.service.create(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{category-id}")
    public ResponseEntity<Void> updatedCategory(@Valid @RequestBody final CategoryRequest request, @PathVariable("category-id") final String id){
        this.service.update(id, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{category-id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable("category-id") final String id){
        return ResponseEntity.ok(this.service.findById(id));
    }

    @DeleteMapping("/{category-id}")
    public  ResponseEntity<Void> deleteCategory( @PathVariable("category-id") final String id){
        this.service.delete(id);
        return ResponseEntity.ok().build();
    }
}
