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
    private String firstName;
    private String lastName;

}
