package com.kanban.kanbanProject.service;

import com.kanban.kanbanProject.config.JWTService;
import com.kanban.kanbanProject.dto.UserDTO;
import com.kanban.kanbanProject.dto.UserLoginDTO;
import com.kanban.kanbanProject.entity.Users;
import com.kanban.kanbanProject.exceptions.KanbanException;
import com.kanban.kanbanProject.exceptions.UserNotFoundException;
import com.kanban.kanbanProject.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private JWTService jwtService;

    // Register
    public void register(UserLoginDTO userDTO) {
        Users user = new Users();
        user.setFirstName(userDTO.getFirstName());
        user.setLastName(userDTO.getLastName());
        user.setEmailId(userDTO.getEmailId());

        String hashedPassword = hashPassword(userDTO.getPassword());
        user.setPassword(hashedPassword);

        usersRepo.save(user);
    }

    // Login (Server must generate a token and return when user logs in)
    public String login(UserLoginDTO userLoginDTO) {
        Users existing = usersRepo.findByEmailId(userLoginDTO.getEmailId());

        System.out.println("User = " + existing);
        System.out.println("Input Password = " + userLoginDTO.getPassword());

        if (existing == null) {
            throw new UserNotFoundException("User not found");
        }

        System.out.println("DB Password = " + existing.getPassword());

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        boolean matches =
                encoder.matches(userLoginDTO.getPassword(),
                        existing.getPassword());

        System.out.println("Password Matches = " + matches);

        if (!matches) {
            throw new UserNotFoundException("Invalid Credentials");
        }

        return jwtService.generateToken(
                existing.getEmailId(),
                existing.getFirstName(),
                existing.getLastName()
        );
    }

    // Get user by user first name + last name
    public List<UserDTO> getUserByName(String firstName) {
        List<Users> usersFound = usersRepo.findByFirstNameContainingIgnoreCase(firstName);

        if (usersFound == null) {
            throw new KanbanException("User not found", HttpStatus.NOT_FOUND);
        }

        List<UserDTO> dtos = new ArrayList<>();

        for (Users user : usersFound) {
            UserDTO dto = new UserDTO();
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setEmailId(user.getEmailId());
            dtos.add(dto);
        }

        return dtos;

    }

    // Hash Password
    private String hashPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }
}
