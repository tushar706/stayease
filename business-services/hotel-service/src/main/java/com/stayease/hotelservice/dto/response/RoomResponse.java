
package com.stayease.hotelservice.dto.response;

import com.stayease.hotelservice.entity.Room;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private Long id;
    private Long hotelId;
    private String roomNumber;
    private Room.RoomType roomType;
    private BigDecimal price;
    private Integer maxOccupancy;
    private Boolean isAvailable;
    private LocalDateTime createdAt;
}