package com.kanban.kanbanProject.service;

import com.kanban.kanbanProject.config.JWTService;
import com.kanban.kanbanProject.dto.UserLoginDTO;
import com.kanban.kanbanProject.entity.Users;
import com.kanban.kanbanProject.repository.UsersRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UsersRepo usersRepo;

    @Mock
    private JWTService jwtService;

    @InjectMocks
    private UserService userService;

    @Test
    void registerShouldSaveUserWithHashedPassword() {

        // Arrange
        UserLoginDTO dto = new UserLoginDTO();

        dto.setFirstName("Ritika");
        dto.setLastName("Kanojia");
        dto.setEmailId("ritika@gmail.com");
        dto.setPassword("password123");

        // Act
        userService.register(dto);

        // Assert + Verify
        ArgumentCaptor<Users> captor =
                ArgumentCaptor.forClass(Users.class);

        verify(usersRepo).save(captor.capture());

        Users savedUser = captor.getValue();

        assertEquals("Ritika", savedUser.getFirstName());
        assertEquals("Kanojia", savedUser.getLastName());
        assertEquals("ritika@gmail.com", savedUser.getEmailId());

        // Most important password check
        assertNotEquals("password123", savedUser.getPassword());

    }

    @Test
    void loginShouldReturnTokenWhenCredentialsAreCorrect() {

        // Arrange
        Users user = new Users();

        user.setEmailId("ritika@gmail.com");
        user.setFirstName("Ritika");
        user.setLastName("Kanojia");

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        user.setPassword(encoder.encode("password123"));

        UserLoginDTO dto = new UserLoginDTO();

        dto.setEmailId("ritika@gmail.com");
        dto.setPassword("password123");


        // Tell mocked repository what to return
        when(usersRepo.findByEmailId("ritika@gmail.com"))
                .thenReturn(user);


        // Tell mocked JWT service what to return
        when(jwtService.generateToken(
                "ritika@gmail.com",
                "Ritika",
                "Kanojia"
        )).thenReturn("fake-jwt-token");


        // Act
        String result = userService.login(dto);

        // Assert
        assertEquals("fake-jwt-token", result);

        // Verify
        verify(usersRepo).findByEmailId("ritika@gmail.com");

        verify(jwtService)
                .generateToken(
                        "ritika@gmail.com",
                        "Ritika",
                        "Kanojia"
                );
    }
}