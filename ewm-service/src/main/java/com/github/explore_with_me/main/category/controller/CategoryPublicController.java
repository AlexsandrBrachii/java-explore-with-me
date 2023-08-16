package com.github.explore_with_me.main.category.controller;

import com.github.explore_with_me.main.category.dto.CategoryOutDto;
import com.github.explore_with_me.main.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequestMapping("/categories")
@RequiredArgsConstructor
@RestController
public class CategoryPublicController {

    private final CategoryService categoryService;

    @GetMapping()
    public List<CategoryOutDto> getCategories(@RequestParam(defaultValue = "0") int from,
                                              @RequestParam(defaultValue = "10") int size) {

        return categoryService.getCategories(from,size);
    }

    @GetMapping("/{id}")
    public CategoryOutDto getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }
}
