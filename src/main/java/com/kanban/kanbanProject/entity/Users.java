package com.kanban.kanbanProject.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String emailId;

    private String password;

    @OneToMany(mappedBy = "users", cascade = CascadeType.ALL, fetch = FetchType.LAZY )
    private List<Boards> boards;

    // Getters
    public String getEmailId() {
        return emailId;
    }

    public String getPassword() {
        return password;
    }
    // Setters
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
