package com.quickbuild.repository;

import com.quickbuild.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    List<User> findByLocationProvinceCode(String code);
    List<User> findByLocationProvinceName(String name);
}
