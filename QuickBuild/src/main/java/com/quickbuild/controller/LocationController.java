package com.quickbuild.controller;

import com.quickbuild.domain.Location;
import com.quickbuild.domain.enums.ELocationType;
import com.quickbuild.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    @PostMapping
    public ResponseEntity<Location> createLocation(@RequestBody Location location,
            @RequestParam(required = false) Long parentId) {
        return new ResponseEntity<>(locationService.saveLocation(location, parentId), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<Location>> getAllLocations() {
        return ResponseEntity.ok(locationService.getAllLocations());
    }

    @GetMapping("/type/{type}")
    public ResponseEntity<List<Location>> getLocationsByType(@PathVariable ELocationType type) {
        return ResponseEntity.ok(locationService.getLocationsByType(type));
    }

    @GetMapping("/children/{parentId}")
    public ResponseEntity<List<Location>> getLocationsByParent(@PathVariable Long parentId) {
        return ResponseEntity.ok(locationService.getLocationsByParent(parentId));
    }
}
