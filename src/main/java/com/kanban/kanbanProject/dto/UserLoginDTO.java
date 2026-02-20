package com.kanban.kanbanProject.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class UserLoginDTO {

    private String emailId;
    private String password;

    // Getters
//    public String getEmailId() {
//        return emailId;
//    }
//
//    public String getPassword() {
//        return password;
//    }
//
//    // Setters
//    public void setEmailId(String emailId) {
//        this.emailId = emailId;
//    }
//
//    public void setPassword(String password) {
//        this.password = password;
//    }

}
