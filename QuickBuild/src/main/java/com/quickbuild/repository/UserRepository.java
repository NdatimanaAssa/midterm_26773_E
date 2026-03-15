package com.quickbuild.repository;

import com.quickbuild.domain.User;
import com.quickbuild.domain.enums.ELocationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);

    @Query("SELECT u FROM User u " +
            "LEFT JOIN u.village v " +
            "LEFT JOIN v.parent cell " +
            "LEFT JOIN cell.parent sector " +
            "LEFT JOIN sector.parent district " +
            "LEFT JOIN district.parent province " +
            "WHERE (v.code = :code AND v.type = :type) " +
            "   OR (cell.code = :code AND cell.type = :type) " +
            "   OR (sector.code = :code AND sector.type = :type) " +
            "   OR (district.code = :code AND district.type = :type) " +
            "   OR (province.code = :code AND province.type = :type)")
    List<User> findUsersByLocationHierarchy(@Param("type") ELocationType type, @Param("code") String code);
}
