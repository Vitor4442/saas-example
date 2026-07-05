package com.vtr.saas.controllers;

import com.vtr.saas.common.PageResponse;
import com.vtr.saas.requests.CategoryRequest;
import com.vtr.saas.responses.CategoryResponse;
import com.vtr.saas.services.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Tag(name = "Category", description = "Category API")
public class CategoryController {

    private final CategoryService service;

    @PostMapping
    private ResponseEntity<Void> createCategory(@Valid @RequestBody final CategoryRequest request){
        this.service.create(request);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{category-id}")
    public ResponseEntity<Void> updatedCategory(@Valid @RequestBody final CategoryRequest request, @PathVariable("category-id")  @NotNull(message = "Category ID cannot be null") final String id){
        this.service.update(id, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{category-id}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable("category-id") @NotNull(message = "Category ID cannot be null") final String id){
        return ResponseEntity.ok(this.service.findById(id));
    }

    @DeleteMapping("/{category-id}")
    public  ResponseEntity<Void> deleteCategory( @PathVariable("category-id")  @NotNull(message = "Category ID cannot be null") final String id){
        this.service.delete(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping()
    public ResponseEntity<PageResponse<CategoryResponse>> getAllCategory(@RequestParam(name = "page", defaultValue = "0") final int page, @RequestParam(name = "size", defaultValue = "10") final int size){
        return ResponseEntity.ok(this.service.findAll(page, size));
    }
}
