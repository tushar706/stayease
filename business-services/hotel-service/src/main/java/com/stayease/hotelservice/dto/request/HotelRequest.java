package com.stayease.hotelservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class HotelRequest {

    @NotBlank(message = "Hotel name is required")
    private String name;

    private String description;

    @NotBlank(message = "Address is required")
    private String address;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Country is required")
    private String country;

    private String phone;

    private String email;

    @Min(value = 1, message = "Star rating minimum 1")
    @Max(value = 5, message = "Star rating maximum 5")
    private Integer starRating;
}
