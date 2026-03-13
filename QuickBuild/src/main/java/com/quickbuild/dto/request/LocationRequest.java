package com.quickbuild.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LocationRequest {

    @NotBlank(message = "District is required")
    private String district;

    @NotBlank(message = "Sector is required")
    private String sector;

    private String street;

    // Optional depending on API usage (could provide ID, Code, or Name)
    private String provinceCode;
    private String provinceName;
}
