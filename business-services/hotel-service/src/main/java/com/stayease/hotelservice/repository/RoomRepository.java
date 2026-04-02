package com.stayease.hotelservice.repository;

import com.stayease.hotelservice.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<Room, Long> {

    // Hotel ke saare rooms
    List<Room> findByHotelId(Long hotelId);

    // Hotel ke available rooms
    List<Room> findByHotelIdAndIsAvailable(Long hotelId, Boolean isAvailable);

    // Room type se dhundho
    List<Room> findByHotelIdAndRoomType(Long hotelId, Room.RoomType roomType);

    // Room number already exist karta hai hotel mein
    boolean existsByHotelIdAndRoomNumber(Long hotelId, String roomNumber);
}
