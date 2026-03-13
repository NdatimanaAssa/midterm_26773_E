package com.quickbuild.service.impl;

import com.quickbuild.domain.Location;
import com.quickbuild.domain.Role;
import com.quickbuild.domain.User;
import com.quickbuild.domain.enums.ELocationType;
import com.quickbuild.domain.enums.RoleName;
import com.quickbuild.dto.request.UserRegistrationRequest;
import com.quickbuild.exception.DuplicateResourceException;
import com.quickbuild.exception.ResourceNotFoundException;
import com.quickbuild.repository.LocationRepository;
import com.quickbuild.repository.RoleRepository;
import com.quickbuild.repository.UserRepository;
import com.quickbuild.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final LocationRepository locationRepository;

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
        Location village = null;
        if (request.getLocation() != null) {
            if (request.getLocation().getVillageCode() != null) {
                village = locationRepository.findByCode(request.getLocation().getVillageCode())
                        .orElseThrow(() -> new ResourceNotFoundException("Village code not found"));
            } else if (request.getLocation().getVillageName() != null) {
                village = locationRepository.findByName(request.getLocation().getVillageName())
                        .orElseThrow(() -> new ResourceNotFoundException("Village name not found"));
            } else {
                throw new IllegalArgumentException("Village code or name must be provided");
            }

            if (village.getType() != ELocationType.VILLAGE) {
                throw new IllegalArgumentException("The provided location is not a VILLAGE");
            }
        } else {
            throw new IllegalArgumentException("Location information is required");
        }

        user.setVillage(village);

        return userRepository.save(user);
    }

    @Override
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }
}
