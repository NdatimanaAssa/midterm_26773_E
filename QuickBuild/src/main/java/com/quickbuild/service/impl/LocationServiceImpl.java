package com.quickbuild.service.impl;

import com.quickbuild.domain.Location;
import com.quickbuild.domain.enums.ELocationType;
import com.quickbuild.exception.DuplicateResourceException;
import com.quickbuild.exception.ResourceNotFoundException;
import com.quickbuild.repository.LocationRepository;
import com.quickbuild.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;

    @Override
    @Transactional
    public Location saveLocation(Location location, Long parentId) {
        if (locationRepository.existsByCode(location.getCode())) {
            throw new DuplicateResourceException("Location code already exists: " + location.getCode());
        }

        if (location.getType() == ELocationType.PROVINCE) {
            if (parentId != null) {
                throw new IllegalArgumentException("Province cannot have a parent location.");
            }
        } else {
            if (parentId == null) {
                throw new IllegalArgumentException(location.getType() + " must have a parent location.");
            }
            Location parent = locationRepository.findById(parentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Parent location not found with ID: " + parentId));

            // Validate hierarchy
            validateHierarchy(location.getType(), parent.getType());
            location.setParent(parent);
        }

        return locationRepository.save(location);
    }

    private void validateHierarchy(ELocationType childType, ELocationType parentType) {
        boolean isValid = false;
        switch (childType) {
            case DISTRICT:
                isValid = (parentType == ELocationType.PROVINCE);
                break;
            case SECTOR:
                isValid = (parentType == ELocationType.DISTRICT);
                break;
            case CELL:
                isValid = (parentType == ELocationType.SECTOR);
                break;
            case VILLAGE:
                isValid = (parentType == ELocationType.CELL);
                break;
            default:
                break;
        }

        if (!isValid) {
            throw new IllegalArgumentException(
                    "Invalid hierarchy: " + parentType + " cannot be the parent of " + childType);
        }
    }

    @Override
    public List<Location> getAllLocations() {
        return locationRepository.findAll();
    }

    @Override
    public List<Location> getLocationsByType(ELocationType type) {
        return locationRepository.findAll().stream()
                .filter(loc -> loc.getType() == type)
                .collect(Collectors.toList());
    }

    @Override
    public List<Location> getLocationsByParent(Long parentId) {
        Location parent = locationRepository.findById(parentId)
                .orElseThrow(() -> new ResourceNotFoundException("Parent location not found"));
        return locationRepository.findAll().stream()
                .filter(loc -> loc.getParent() != null && loc.getParent().getId().equals(parent.getId()))
                .collect(Collectors.toList());
    }
}
