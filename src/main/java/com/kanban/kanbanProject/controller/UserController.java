package com.kanban.kanbanProject.controller;

import com.kanban.kanbanProject.dto.UserLoginDTO;
import com.kanban.kanbanProject.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public void userRegister(@RequestBody UserLoginDTO userLoginDTO) {
        userService.register(userLoginDTO);
    }

    @PostMapping("/login")
    public ResponseEntity<?> userLogin(@RequestBody UserLoginDTO userLoginDTO) {
        String token = userService.login(userLoginDTO);
        return ResponseEntity.ok(Map.of("token", token));
    }
}
