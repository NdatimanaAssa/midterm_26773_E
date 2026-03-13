package com.quickbuild.service.impl;

import com.quickbuild.domain.User;
import com.quickbuild.domain.Location;
import com.quickbuild.domain.Province;
import com.quickbuild.domain.Role;
import com.quickbuild.domain.enums.RoleName;
import com.quickbuild.dto.request.UserRegistrationRequest;
import com.quickbuild.exception.DuplicateResourceException;
import com.quickbuild.exception.ResourceNotFoundException;
import com.quickbuild.repository.ProvinceRepository;
import com.quickbuild.repository.RoleRepository;
import com.quickbuild.repository.UserRepository;
import com.quickbuild.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final ProvinceRepository provinceRepository;

    @Override
    @Transactional
    public User registerUser(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException("Email is already taken.");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPhone(request.getPhone());
        user.setPassword(request.getPassword()); // Should be encoded in a real app

        // Role assignment
        Role customerRole = roleRepository.findByName(RoleName.CUSTOMER)
                .orElseGet(() -> roleRepository.save(new Role(null, RoleName.CUSTOMER)));
        user.setRoles(Collections.singleton(customerRole));

        // Location setup
        Location location = new Location();
        location.setDistrict(request.getLocation().getDistrict());
        location.setSector(request.getLocation().getSector());
        location.setStreet(request.getLocation().getStreet());

        // We assume province is managed and provided via code or name
        Province province = null;
        if (request.getLocation().getProvinceCode() != null) {
            province = provinceRepository.findAll().stream()
                    .filter(p -> p.getCode().equalsIgnoreCase(request.getLocation().getProvinceCode()))
                    .findFirst().orElseThrow(() -> new ResourceNotFoundException("Province code not found"));
        } else if (request.getLocation().getProvinceName() != null) {
            province = provinceRepository.findAll().stream()
                    .filter(p -> p.getName().equalsIgnoreCase(request.getLocation().getProvinceName()))
                    .findFirst().orElseThrow(() -> new ResourceNotFoundException("Province name not found"));
        } else {
            throw new IllegalArgumentException("Province info must be provided");
        }

        location.setProvince(province);
        user.setLocation(location);

        return userRepository.save(user);
    }

    @Override
    public List<User> getUsersByProvinceCode(String code) {
        return userRepository.findByLocationProvinceCode(code.toUpperCase());
    }

    @Override
    public List<User> getUsersByProvinceName(String name) {
        return userRepository.findByLocationProvinceName(name);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
