package com.github.explore_with_me.main.compilation.service;

import com.github.explore_with_me.main.compilation.dto.CompilationDto;
import com.github.explore_with_me.main.compilation.dto.NewCompilationDto;
import com.github.explore_with_me.main.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto createCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(Long compId, UpdateCompilationRequest updateCompilationRequest);

    CompilationDto getCompilation(Long compId);

    List<CompilationDto> getCompilations(Boolean pinned, int from, int size);
}
