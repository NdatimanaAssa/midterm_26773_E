package com.quickbuild.service;

import com.quickbuild.domain.User;
import com.quickbuild.dto.request.UserRegistrationRequest;

import java.util.List;

public interface UserService {
    User registerUser(UserRegistrationRequest request);

    List<User> getUsersByProvinceCode(String code);

    List<User> getUsersByProvinceName(String name);

    User getUserById(Long id);
}
