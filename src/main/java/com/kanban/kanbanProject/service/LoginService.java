package com.kanban.kanbanProject.service;

import com.kanban.kanbanProject.dto.UserLoginDTO;
import com.kanban.kanbanProject.entity.Users;
import com.kanban.kanbanProject.exceptions.IncorrectDetails;
import com.kanban.kanbanProject.exceptions.UserNotFoundException;
import com.kanban.kanbanProject.repository.UsersRepo;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    @Autowired
    UsersRepo usersRepo;

    public ResponseEntity<?> login(UserLoginDTO userLoginDTO) {

        Optional<Users> userExists = usersRepo.findByEmailId(userLoginDTO.getEmailId());

        if (userExists.isEmpty()) {
            throw new UserNotFoundException("User not found");
        }

        Users user = userExists.get();

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        validateCredentials(userLoginDTO);

        if (!encoder.matches(userLoginDTO.getPassword(), user.getPassword())) {
            throw new UserNotFoundException("Invalid credentials");
        }

        return ResponseEntity.ok("Password Match, welcome " + user.getEmailId());
    }

    public ResponseEntity<?> register(UserLoginDTO userLoginDTO) throws Exception {

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hashedPassword = encoder.encode(userLoginDTO.getPassword());

        validateCredentials(userLoginDTO);

        Users user = new Users();
        user.setEmailId(userLoginDTO.getEmailId());
        user.setPassword(hashedPassword);

        usersRepo.save(user);

        return ResponseEntity.ok("User registered! Please login...");

    }

    private void validateCredentials(UserLoginDTO userLoginDTO) {
        if (userLoginDTO.getEmailId().isEmpty() || userLoginDTO.getPassword().isEmpty()) {
            throw new IncorrectDetails("Details cannot be empty.");
        }

    }
}
