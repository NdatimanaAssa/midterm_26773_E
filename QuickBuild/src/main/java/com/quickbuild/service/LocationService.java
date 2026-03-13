package com.quickbuild.service;

import com.quickbuild.domain.Location;
import com.quickbuild.domain.enums.ELocationType;

import java.util.List;

public interface LocationService {
    Location saveLocation(Location location, Long parentId);

    List<Location> getAllLocations();

    List<Location> getLocationsByType(ELocationType type);

    List<Location> getLocationsByParent(Long parentId);
}
