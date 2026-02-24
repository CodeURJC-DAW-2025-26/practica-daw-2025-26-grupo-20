package es.codeurjc.mokaf.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import es.codeurjc.mokaf.model.User;
import es.codeurjc.mokaf.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("Test User", "test@test.com", "password", User.Role.CUSTOMER);
        testUser.setId(1L);
    }

    @Test
    void testLoadUserByUsernameFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));

        User loadedUser = userService.loadUserByUsername("test@test.com");

        assertNotNull(loadedUser);
        assertEquals("test@test.com", loadedUser.getEmail());
        assertEquals("Test User", loadedUser.getName());
        verify(userRepository, times(1)).findByEmail("test@test.com");
    }

    @Test
    void testLoadUserByUsernameNotFound() {
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> {
            userService.loadUserByUsername("notfound@test.com");
        });

        verify(userRepository, times(1)).findByEmail("notfound@test.com");
    }

    @Test
    void testExistsByEmailTrue() {
        when(userRepository.existsByEmail("test@test.com")).thenReturn(true);

        assertTrue(userService.existsByEmail("test@test.com"));
        verify(userRepository, times(1)).existsByEmail("test@test.com");
    }

    @Test
    void testExistsByEmailFalse() {
        when(userRepository.existsByEmail("new@test.com")).thenReturn(false);

        assertFalse(userService.existsByEmail("new@test.com"));
        verify(userRepository, times(1)).existsByEmail("new@test.com");
    }

    @Test
    void testSaveUser() {
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        User savedUser = userService
                .save(new User("New User", "new@test.com", "pass", User.Role.CUSTOMER));

        assertNotNull(savedUser);
        assertEquals("Test User", savedUser.getName()); // Returns mock so it should match the mock testUser properties
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testDeleteUser() {
        doNothing().when(userRepository).delete(any(User.class));

        userService.delete(testUser);

        verify(userRepository, times(1)).delete(testUser);
    }
}
