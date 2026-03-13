package com.quickbuild.controller;

import com.quickbuild.domain.Province;
import com.quickbuild.service.ProvinceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/provinces")
@RequiredArgsConstructor
public class ProvinceController {

    private final ProvinceService provinceService;

    @GetMapping
    public ResponseEntity<List<Province>> getAllProvinces() {
        return ResponseEntity.ok(provinceService.getAllProvinces());
    }

    @PostMapping
    public ResponseEntity<Province> createProvince(@RequestBody Province province) {
        return new ResponseEntity<>(provinceService.createProvince(province), HttpStatus.CREATED);
    }
}
