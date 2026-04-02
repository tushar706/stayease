package com.stayease.hotelservice.repository;

import com.stayease.hotelservice.entity.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, Long> {


    List<Hotel> findByCityIgnoreCase(String city);

    // City aur star rating se search
    List<Hotel> findByCityIgnoreCaseAndStarRating(String city, Integer starRating);

    // Name se search
    List<Hotel> findByNameContainingIgnoreCase(String name);

    // City aur available rooms wale hotels
    @Query("SELECT DISTINCT h FROM Hotel h " +
            "JOIN h.rooms r " +
            "WHERE LOWER(h.city) = LOWER(:city) " +
            "AND r.isAvailable = true")
    List<Hotel> findAvailableHotelsByCity(@Param("city") String city);
}
