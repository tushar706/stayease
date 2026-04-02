package com.stayease.hotelservice.dto.request;

import com.stayease.hotelservice.entity.Room;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class RoomRequest {

    @NotBlank(message = "Room number is required")
    private String roomNumber;

    @NotNull(message = "Room type is required")
    private Room.RoomType roomType;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Max occupancy is required")
    @Min(value = 1, message = "Max occupancy minimum 1")
    private Integer maxOccupancy;
}
