package com.quickbuild.repository;

import com.quickbuild.domain.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    boolean existsByCode(String code);

    Optional<Location> findByCode(String code);

    Optional<Location> findByName(String name);
}
