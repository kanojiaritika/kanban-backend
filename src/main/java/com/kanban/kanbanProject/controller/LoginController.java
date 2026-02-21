package com.kanban.kanbanProject.controller;

import com.kanban.kanbanProject.dto.UserLoginDTO;
import com.kanban.kanbanProject.exceptions.UserNotFoundException;
import com.kanban.kanbanProject.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    LoginService loginService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginDTO userLoginDTO) throws Exception{
        return loginService.login(userLoginDTO);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserLoginDTO userLoginDTO) throws Exception {
        return loginService.register(userLoginDTO);
    }
}
