package com.quickbuild.service.impl;

import com.quickbuild.domain.Province;
import com.quickbuild.repository.ProvinceRepository;
import com.quickbuild.service.ProvinceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProvinceServiceImpl implements ProvinceService {

    private final ProvinceRepository provinceRepository;

    @Override
    public List<Province> getAllProvinces() {
        return provinceRepository.findAll();
    }

    @Override
    public Province createProvince(Province province) {
        return provinceRepository.save(province);
    }
}
