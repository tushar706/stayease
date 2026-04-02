package com.stayease.hotelservice.controller;

import com.stayease.hotelservice.dto.request.HotelRequest;
import com.stayease.hotelservice.dto.request.RoomRequest;
import com.stayease.hotelservice.dto.response.HotelResponse;
import com.stayease.hotelservice.dto.response.RoomResponse;
import com.stayease.hotelservice.service.HotelService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
@Slf4j
public class HotelController {

    private final HotelService hotelService;

    // ─── Hotel APIs ───────────────────────────────────────

    @PostMapping
    public ResponseEntity<HotelResponse> createHotel(
            @Valid @RequestBody HotelRequest request) {
        log.info("Create hotel request received: {}", request.getName());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(hotelService.createHotel(request));
    }

    @GetMapping
    public ResponseEntity<List<HotelResponse>> getAllHotels() {
        log.info("Get all hotels request received");
        return ResponseEntity.ok(hotelService.getAllHotels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<HotelResponse> getHotelById(
            @PathVariable Long id) {
        log.info("Get hotel by id request received: {}", id);
        return ResponseEntity.ok(hotelService.getHotelById(id));
    }

    @GetMapping("/search")
    public ResponseEntity<List<HotelResponse>> searchByCity(
            @RequestParam String city) {
        log.info("Search hotels by city: {}", city);
        return ResponseEntity.ok(hotelService.searchHotelsByCity(city));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HotelResponse> updateHotel(
            @PathVariable Long id,
            @Valid @RequestBody HotelRequest request) {
        log.info("Update hotel request received: {}", id);
        return ResponseEntity.ok(hotelService.updateHotel(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteHotel(
            @PathVariable Long id) {
        log.info("Delete hotel request received: {}", id);
        hotelService.deleteHotel(id);
        return ResponseEntity.noContent().build();
    }

    // ─── Room APIs ────────────────────────────────────────

    @PostMapping("/{hotelId}/rooms")
    public ResponseEntity<RoomResponse> addRoom(
            @PathVariable Long hotelId,
            @Valid @RequestBody RoomRequest request) {
        log.info("Add room request received for hotel: {}", hotelId);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(hotelService.addRoom(hotelId, request));
    }

    @GetMapping("/{hotelId}/rooms")
    public ResponseEntity<List<RoomResponse>> getRooms(
            @PathVariable Long hotelId) {
        log.info("Get rooms request received for hotel: {}", hotelId);
        return ResponseEntity.ok(hotelService.getRoomsByHotel(hotelId));
    }

    @GetMapping("/{hotelId}/rooms/available")
    public ResponseEntity<List<RoomResponse>> getAvailableRooms(
            @PathVariable Long hotelId) {
        log.info("Get available rooms request for hotel: {}", hotelId);
        return ResponseEntity.ok(hotelService.getAvailableRooms(hotelId));
    }

    @PatchMapping("/rooms/{roomId}/availability")
    public ResponseEntity<Void> updateRoomAvailability(
            @PathVariable Long roomId,
            @RequestParam Boolean isAvailable) {
        log.info("Update room availability: {} to {}", roomId, isAvailable);
        hotelService.updateRoomAvailability(roomId, isAvailable);
        return ResponseEntity.ok().build();
    }
}




