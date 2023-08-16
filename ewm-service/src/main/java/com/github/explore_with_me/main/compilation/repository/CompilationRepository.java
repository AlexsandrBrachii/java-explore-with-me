package com.github.explore_with_me.main.compilation.repository;


import java.util.List;

import com.github.explore_with_me.main.compilation.model.Compilation;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CompilationRepository extends JpaRepository<Compilation, Long>,
        PagingAndSortingRepository<Compilation, Long> {

    @Query("select c from Compilation as c "
            + "left join fetch c.event as e "
            + "where c.pinned = :pinned")
    List<Compilation> findAllByPinned(@Param("pinned") boolean pinned, Pageable pagination);
}
