package com.stayease.hotelservice.service;

import com.stayease.hotelservice.dto.request.HotelRequest;
import com.stayease.hotelservice.dto.request.RoomRequest;
import com.stayease.hotelservice.dto.response.HotelResponse;
import com.stayease.hotelservice.dto.response.RoomResponse;
import com.stayease.hotelservice.entity.Hotel;
import com.stayease.hotelservice.entity.Room;
import com.stayease.hotelservice.exception.HotelNotFoundException;
import com.stayease.hotelservice.exception.RoomAlreadyExistsException;
import com.stayease.hotelservice.exception.RoomNotFoundException;
import com.stayease.hotelservice.repository.HotelRepository;
import com.stayease.hotelservice.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HotelService {

    private final HotelRepository hotelRepository;
    private final RoomRepository roomRepository;

    // ─── Hotel CRUD ───────────────────────────────────────

    @Transactional
    public HotelResponse createHotel(HotelRequest request) {
        log.info("Creating hotel: {}", request.getName());

        Hotel hotel = Hotel.builder()
                .name(request.getName())
                .description(request.getDescription())
                .address(request.getAddress())
                .city(request.getCity())
                .state(request.getState())
                .country(request.getCountry())
                .phone(request.getPhone())
                .email(request.getEmail())
                .starRating(request.getStarRating())
                .build();

        Hotel savedHotel = hotelRepository.save(hotel);
        log.info("Hotel created with id: {}", savedHotel.getId());

        return mapToHotelResponse(savedHotel);
    }

    public List<HotelResponse> getAllHotels() {
        log.info("Fetching all hotels");
        return hotelRepository.findAll()
                .stream()
                .map(this::mapToHotelResponse)
                .collect(Collectors.toList());
    }

    public HotelResponse getHotelById(Long id) {
        log.info("Fetching hotel with id: {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(
                        "Hotel not found with id: " + id));
        return mapToHotelResponse(hotel);
    }

    public List<HotelResponse> searchHotelsByCity(String city) {
        log.info("Searching hotels in city: {}", city);
        return hotelRepository.findAvailableHotelsByCity(city)
                .stream()
                .map(this::mapToHotelResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public HotelResponse updateHotel(Long id, HotelRequest request) {
        log.info("Updating hotel with id: {}", id);

        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(
                        "Hotel not found with id: " + id));

        hotel.setName(request.getName());
        hotel.setDescription(request.getDescription());
        hotel.setAddress(request.getAddress());
        hotel.setCity(request.getCity());
        hotel.setState(request.getState());
        hotel.setCountry(request.getCountry());
        hotel.setPhone(request.getPhone());
        hotel.setEmail(request.getEmail());
        hotel.setStarRating(request.getStarRating());

        Hotel updatedHotel = hotelRepository.save(hotel);
        log.info("Hotel updated successfully: {}", id);

        return mapToHotelResponse(updatedHotel);
    }

    @Transactional
    public void deleteHotel(Long id) {
        log.info("Deleting hotel with id: {}", id);
        Hotel hotel = hotelRepository.findById(id)
                .orElseThrow(() -> new HotelNotFoundException(
                        "Hotel not found with id: " + id));
        hotelRepository.delete(hotel);
        log.info("Hotel deleted successfully: {}", id);
    }

    // ─── Room Management ──────────────────────────────────

    @Transactional
    public RoomResponse addRoom(Long hotelId, RoomRequest request) {
        log.info("Adding room to hotel: {}", hotelId);

        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new HotelNotFoundException(
                        "Hotel not found with id: " + hotelId));

        // Duplicate room number check
        if (roomRepository.existsByHotelIdAndRoomNumber(
                hotelId, request.getRoomNumber())) {
            throw new RoomAlreadyExistsException(
                    "Room number already exists: " + request.getRoomNumber());
        }

        Room room = Room.builder()
                .hotel(hotel)
                .roomNumber(request.getRoomNumber())
                .roomType(request.getRoomType())
                .price(request.getPrice())
                .maxOccupancy(request.getMaxOccupancy())
                .isAvailable(true)
                .build();

        Room savedRoom = roomRepository.save(room);
        log.info("Room added with id: {}", savedRoom.getId());

        return mapToRoomResponse(savedRoom);
    }

    public List<RoomResponse> getRoomsByHotel(Long hotelId) {
        log.info("Fetching rooms for hotel: {}", hotelId);

        if (!hotelRepository.existsById(hotelId)) {
            throw new HotelNotFoundException(
                    "Hotel not found with id: " + hotelId);
        }

        return roomRepository.findByHotelId(hotelId)
                .stream()
                .map(this::mapToRoomResponse)
                .collect(Collectors.toList());
    }

    public List<RoomResponse> getAvailableRooms(Long hotelId) {
        log.info("Fetching available rooms for hotel: {}", hotelId);

        if (!hotelRepository.existsById(hotelId)) {
            throw new HotelNotFoundException(
                    "Hotel not found with id: " + hotelId);
        }

        return roomRepository.findByHotelIdAndIsAvailable(hotelId, true)
                .stream()
                .map(this::mapToRoomResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void updateRoomAvailability(Long roomId, Boolean isAvailable) {
        log.info("Updating room availability: {} to {}", roomId, isAvailable);

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RoomNotFoundException(
                        "Room not found with id: " + roomId));

        room.setIsAvailable(isAvailable);
        roomRepository.save(room);
    }

    // ─── Mappers ──────────────────────────────────────────

    private HotelResponse mapToHotelResponse(Hotel hotel) {
        List<RoomResponse> rooms = hotel.getRooms() == null ? List.of() :
                hotel.getRooms()
                        .stream()
                        .map(this::mapToRoomResponse)
                        .collect(Collectors.toList());

        return HotelResponse.builder()
                .id(hotel.getId())
                .name(hotel.getName())
                .description(hotel.getDescription())
                .address(hotel.getAddress())
                .city(hotel.getCity())
                .state(hotel.getState())
                .country(hotel.getCountry())
                .phone(hotel.getPhone())
                .email(hotel.getEmail())
                .starRating(hotel.getStarRating())
                .rooms(rooms)
                .createdAt(hotel.getCreatedAt())
                .build();
    }

    private RoomResponse mapToRoomResponse(Room room) {
        return RoomResponse.builder()
                .id(room.getId())
                .hotelId(room.getHotel().getId())
                .roomNumber(room.getRoomNumber())
                .roomType(room.getRoomType())
                .price(room.getPrice())
                .maxOccupancy(room.getMaxOccupancy())
                .isAvailable(room.getIsAvailable())
                .createdAt(room.getCreatedAt())
                .build();
    }
}

