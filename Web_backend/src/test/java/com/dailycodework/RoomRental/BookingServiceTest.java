package com.dailycodework.RoomRental;

import com.dailycodework.RoomRental.exception.InvalidBookingRequestException;
import com.dailycodework.RoomRental.exception.ResourceNotFoundException;
import com.dailycodework.RoomRental.model.BookedRoom;
import com.dailycodework.RoomRental.model.Room;
import com.dailycodework.RoomRental.repository.BookingRepository;
import com.dailycodework.RoomRental.service.BookingService;
import com.dailycodework.RoomRental.service.IRoomService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private IRoomService roomService;

    @InjectMocks
    private BookingService bookingService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getAllBookings_Success() {
        List<BookedRoom> bookings = new ArrayList<>();
        bookings.add(new BookedRoom());
        bookings.add(new BookedRoom());

        when(bookingRepository.findAll()).thenReturn(bookings);

        List<BookedRoom> result = bookingService.getAllBookings();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(bookingRepository, times(1)).findAll();
    }

    @Test
    void getBookingsByUserEmail_Success() {
        String email = "test@example.com";
        List<BookedRoom> bookings = new ArrayList<>();
        bookings.add(new BookedRoom());
        bookings.add(new BookedRoom());

        when(bookingRepository.findByGuestEmail(email)).thenReturn(bookings);

        List<BookedRoom> result = bookingService.getBookingsByUserEmail(email);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(bookingRepository, times(1)).findByGuestEmail(email);
    }

    @Test
    void cancelBooking_Success() {
        Long bookingId = 1L;

        bookingService.cancelBooking(bookingId);

        verify(bookingRepository, times(1)).deleteById(bookingId);
    }

    @Test
    void getAllBookingsByRoomId_Success() {
        Long roomId = 1L;
        List<BookedRoom> bookings = new ArrayList<>();
        bookings.add(new BookedRoom());
        bookings.add(new BookedRoom());

        when(bookingRepository.findByRoomId(roomId)).thenReturn(bookings);

        List<BookedRoom> result = bookingService.getAllBookingsByRoomId(roomId);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(bookingRepository, times(1)).findByRoomId(roomId);
    }



    @Test
    void saveBooking_InvalidBookingRequestException() {
        Long roomId = 1L;
        BookedRoom bookingRequest = new BookedRoom();
        bookingRequest.setCheckInDate(LocalDate.now().plusDays(1));
        bookingRequest.setCheckOutDate(LocalDate.now());

        assertThrows(InvalidBookingRequestException.class, () -> bookingService.saveBooking(roomId, bookingRequest));
        verify(roomService, never()).getRoomById(roomId);
        verify(bookingRepository, never()).save(any(BookedRoom.class));
    }


    @Test
    void findByBookingConfirmationCode_Success() {
        String confirmationCode = "ABC123";
        BookedRoom bookedRoom = new BookedRoom();
        bookedRoom.setBookingConfirmationCode(confirmationCode);

        when(bookingRepository.findByBookingConfirmationCode(confirmationCode)).thenReturn(Optional.of(bookedRoom));

        BookedRoom result = bookingService.findByBookingConfirmationCode(confirmationCode);

        assertNotNull(result);
        assertEquals(confirmationCode, result.getBookingConfirmationCode());
        verify(bookingRepository, times(1)).findByBookingConfirmationCode(confirmationCode);
    }

    @Test
    void findByBookingConfirmationCode_ResourceNotFoundException() {
        String confirmationCode = "XYZ456";

        when(bookingRepository.findByBookingConfirmationCode(confirmationCode)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> bookingService.findByBookingConfirmationCode(confirmationCode));
        verify(bookingRepository, times(1)).findByBookingConfirmationCode(confirmationCode);
    }
}
