package com.kanban.kanbanProject.service;

import com.kanban.kanbanProject.dto.UserLoginDTO;
import com.kanban.kanbanProject.entity.Users;
import com.kanban.kanbanProject.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class LoginService {

    @Autowired
    UsersRepo usersRepo;

    public ResponseEntity<String> login(UserLoginDTO userLoginDTO) throws Exception {

        Optional<Users> userExists = usersRepo.findByEmailId(userLoginDTO.getEmailId());
        if (userExists.isEmpty()) {
            throw new Exception("User not found. Please Register!");
        }
        return ResponseEntity.ok("Welcome, " + userLoginDTO.getEmailId());

    }

    public ResponseEntity<String> register(UserLoginDTO userLoginDTO) throws Exception {

        validateCredentials(userLoginDTO);

        Users user = new Users();
        user.setEmailId(userLoginDTO.getEmailId());
        user.setPassword(userLoginDTO.getPassword());

        usersRepo.save(user);

        return ResponseEntity.ok("User registered! Please login...");

    }

    private void validateCredentials(UserLoginDTO userLoginDTO) throws Exception {
        if (userLoginDTO.getEmailId().isEmpty() || userLoginDTO.getPassword().isEmpty()) {
            throw new Exception("Details cannot be empty.");
        }

    }
}
