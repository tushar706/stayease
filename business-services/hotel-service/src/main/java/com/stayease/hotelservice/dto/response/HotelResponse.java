package com.stayease.hotelservice.dto.response;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HotelResponse {
    private Long id;
    private String name;
    private String description;
    private String address;
    private String city;
    private String state;
    private String country;
    private String phone;
    private String email;
    private Integer starRating;
    private List<RoomResponse> rooms;
    private LocalDateTime createdAt;
}
