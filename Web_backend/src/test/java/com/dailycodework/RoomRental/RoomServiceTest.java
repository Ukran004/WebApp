package com.dailycodework.RoomRental;

import com.dailycodework.RoomRental.model.Room;
import com.dailycodework.RoomRental.repository.RoomRepository;
import com.dailycodework.RoomRental.service.RoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private RoomService roomService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void addNewRoom_Success() throws SQLException, IOException {
        MultipartFile file = new MockMultipartFile("test.jpg", new byte[0]);
        String roomType = "Single";
        BigDecimal roomPrice = BigDecimal.valueOf(100);

        Room room = new Room();
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);

        when(roomRepository.save(any(Room.class))).thenReturn(room);

        Room savedRoom = roomService.addNewRoom(file, roomType, roomPrice);

        assertNotNull(savedRoom);
        assertEquals(roomType, savedRoom.getRoomType());
        assertEquals(roomPrice, savedRoom.getRoomPrice());
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void getAllRoomTypes_Success() {
        List<String> roomTypes = new ArrayList<>();
        roomTypes.add("Single");
        roomTypes.add("Double");

        when(roomRepository.findDistinctRoomTypes()).thenReturn(roomTypes);

        List<String> result = roomService.getAllRoomTypes();

        assertNotNull(result);
        assertEquals(2, result.size());
        assertTrue(result.contains("Single"));
        assertTrue(result.contains("Double"));
        verify(roomRepository, times(1)).findDistinctRoomTypes();
    }

    @Test
    void getAllRooms_Success() {
        List<Room> rooms = new ArrayList<>();
        rooms.add(new Room());
        rooms.add(new Room());

        when(roomRepository.findAll()).thenReturn(rooms);

        List<Room> result = roomService.getAllRooms();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(roomRepository, times(1)).findAll();
    }

    @Test
    void getRoomPhotoByRoomId_Success() throws SQLException {
        Long roomId = 1L;
        Room room = new Room();
        room.setId(roomId);
        Blob photoBlob = null; // mock blob
        room.setPhoto(photoBlob);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        byte[] result = roomService.getRoomPhotoByRoomId(roomId);

        assertNull(result); // As photoBlob is null
        verify(roomRepository, times(1)).findById(roomId);
    }

    @Test
    void deleteRoom_Success() {
        Long roomId = 1L;
        Room room = new Room();
        room.setId(roomId);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        roomService.deleteRoom(roomId);

        verify(roomRepository, times(1)).deleteById(roomId);
    }

    @Test
    void updateRoom_Success() throws SQLException {
        Long roomId = 1L;
        String roomType = "Single";
        BigDecimal roomPrice = BigDecimal.valueOf(100);
        byte[] photoBytes = new byte[0];

        Room room = new Room();
        room.setId(roomId);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));
        when(roomRepository.save(any(Room.class))).thenReturn(room);

        Room updatedRoom = roomService.updateRoom(roomId, roomType, roomPrice, photoBytes);

        assertNotNull(updatedRoom);
        assertEquals(roomType, updatedRoom.getRoomType());
        assertEquals(roomPrice, updatedRoom.getRoomPrice());
        // For brevity, not checking photo update
        verify(roomRepository, times(1)).findById(roomId);
        verify(roomRepository, times(1)).save(any(Room.class));
    }

    @Test
    void getRoomById_Success() {
        Long roomId = 1L;
        Room room = new Room();
        room.setId(roomId);

        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        Optional<Room> result = roomService.getRoomById(roomId);

        assertTrue(result.isPresent());
        assertEquals(roomId, result.get().getId());
        verify(roomRepository, times(1)).findById(roomId);
    }

    @Test
    void getAvailableRooms_Success() {
        // Provide implementation for this test according to your business logic
        // You may need to mock some dependencies and set up test data
    }
}
