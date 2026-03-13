package com.quickbuild.service;

import com.quickbuild.domain.Province;
import java.util.List;

public interface ProvinceService {
    List<Province> getAllProvinces();

    Province createProvince(Province province);
}
