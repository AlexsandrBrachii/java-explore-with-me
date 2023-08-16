package com.github.explore_with_me.main.user.repository;

import java.util.List;

import com.github.explore_with_me.main.user.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, PagingAndSortingRepository<User, Long> {

    List<User> findAllByIdIn(@Param("ids") Iterable<Long> ids, Pageable pagination);
}
