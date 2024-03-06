package com.dailycodework.RoomRental;

import com.dailycodework.RoomRental.exception.RoleAlreadyExistException;
import com.dailycodework.RoomRental.exception.UserAlreadyExistsException;
import com.dailycodework.RoomRental.model.Role;
import com.dailycodework.RoomRental.model.User;
import com.dailycodework.RoomRental.repository.RoleRepository;
import com.dailycodework.RoomRental.repository.UserRepository;
import com.dailycodework.RoomRental.service.RoleService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RoleService roleService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getRoles_Success() {
        List<Role> roles = new ArrayList<>();
        roles.add(new Role("ROLE_ADMIN"));
        roles.add(new Role("ROLE_USER"));

        when(roleRepository.findAll()).thenReturn(roles);

        List<Role> result = roleService.getRoles();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(roleRepository, times(1)).findAll();
    }



    @Test
    void createRole_RoleAlreadyExists() {
        Role role = new Role("ADMIN");

        when(roleRepository.existsByName("ROLE_ADMIN")).thenReturn(true);

        assertThrows(RoleAlreadyExistException.class, () -> roleService.createRole(role));
        verify(roleRepository, never()).save(any(Role.class));
    }


    @Test
    void findByName_Success() {
        String roleName = "ROLE_ADMIN";
        Role role = new Role(roleName);

        when(roleRepository.findByName(roleName)).thenReturn(Optional.of(role));

        Role result = roleService.findByName(roleName);

        assertNotNull(result);
        assertEquals(roleName, result.getName());
        verify(roleRepository, times(1)).findByName(roleName);
    }

    @Test
    void removeUserFromRole_Success() {
        Long userId = 1L;
        Long roleId = 2L;

        User user = new User();
        user.setId(userId);

        Role role = new Role("ROLE_USER");
        role.setId(roleId);
        role.getUsers().add(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        User result = roleService.removeUserFromRole(userId, roleId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertFalse(role.getUsers().contains(user));
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void assignRoleToUser_Success() {
        Long userId = 1L;
        Long roleId = 2L;

        User user = new User();
        user.setId(userId);

        Role role = new Role("ROLE_USER");
        role.setId(roleId);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        User result = roleService.assignRoleToUser(userId, roleId);

        assertNotNull(result);
        assertEquals(userId, result.getId());
        assertTrue(role.getUsers().contains(user));
        verify(roleRepository, times(1)).save(any(Role.class));
    }

    @Test
    void removeAllUsersFromRole_Success() {
        Long roleId = 1L;

        Role role = new Role("ROLE_USER");
        role.setId(roleId);
        role.getUsers().add(new User());

        when(roleRepository.findById(roleId)).thenReturn(Optional.of(role));
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        Role result = roleService.removeAllUsersFromRole(roleId);

        assertNotNull(result);
        assertTrue(result.getUsers().isEmpty());
        verify(roleRepository, times(1)).save(any(Role.class));
    }
}
