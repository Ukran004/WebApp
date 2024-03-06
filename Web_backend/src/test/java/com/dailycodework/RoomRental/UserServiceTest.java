package com.dailycodework.RoomRental;

import com.dailycodework.RoomRental.exception.UserAlreadyExistsException;
import com.dailycodework.RoomRental.model.Role;
import com.dailycodework.RoomRental.model.User;
import com.dailycodework.RoomRental.repository.RoleRepository;
import com.dailycodework.RoomRental.repository.UserRepository;
import com.dailycodework.RoomRental.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void registerUser_Success() {
        String email = "test@example.com";
        String password = "password";
        User user = new User();
        user.setEmail(email);
        user.setPassword(password);

        when(userRepository.existsByEmail(email)).thenReturn(false);
        when(passwordEncoder.encode(password)).thenReturn("encodedPassword");

        Role role = new Role();
        role.setId(1L);
        role.setName("ROLE_USER");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));

        when(userRepository.save(any(User.class))).thenReturn(user);

        User savedUser = userService.registerUser(user);

        assertNotNull(savedUser);
        assertEquals(email, savedUser.getEmail());
        assertEquals("encodedPassword", savedUser.getPassword());
        assertEquals(1, savedUser.getRoles().size());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void registerUser_UserAlreadyExists() {
        String email = "existing@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.existsByEmail(email)).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.registerUser(user));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getUsers_Success() {
        List<User> userList = new ArrayList<>();
        userList.add(new User());
        userList.add(new User());

        when(userRepository.findAll()).thenReturn(userList);

        List<User> result = userService.getUsers();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void deleteUser_UserExists() {
        String email = "existing@example.com";

        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        userService.deleteUser(email);

        verify(userRepository, times(1)).deleteByEmail(email);
    }

    @Test
    void deleteUser_UserNotFound() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.deleteUser(email));
        verify(userRepository, never()).deleteByEmail(email);
    }

    @Test
    void getUser_Success() {
        String email = "test@example.com";
        User user = new User();
        user.setEmail(email);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        User result = userService.getUser(email);

        assertNotNull(result);
        assertEquals(email, result.getEmail());
        verify(userRepository, times(1)).findByEmail(email);
    }

    @Test
    void getUser_UserNotFound() {
        String email = "nonexistent@example.com";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.getUser(email));
        verify(userRepository, times(1)).findByEmail(email);
    }
}
