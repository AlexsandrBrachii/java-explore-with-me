package com.github.explore_with_me.main.event.repository;

import com.github.explore_with_me.main.event.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

}