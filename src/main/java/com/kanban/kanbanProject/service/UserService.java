package com.kanban.kanbanProject.service;

import com.kanban.kanbanProject.config.JWTService;
import com.kanban.kanbanProject.dto.UserLoginDTO;
import com.kanban.kanbanProject.entity.Users;
import com.kanban.kanbanProject.exceptions.UserNotFoundException;
import com.kanban.kanbanProject.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private JWTService jwtService;

    // Register
    public void register(UserLoginDTO userDTO) {
        Users user = new Users();
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
            throw new UserNotFoundException("Password does not match");
        }

        return jwtService.generateToken(userLoginDTO.getEmailId());
    }

    // Hash Password
    private String hashPassword(String password) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return encoder.encode(password);
    }
}
