package com.kanban.kanbanProject.dto;

public class UserLoginDTO {

    private String emailId;
    private String password;

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
