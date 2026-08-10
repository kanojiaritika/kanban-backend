package com.kanban.kanbanProject.dto;

import com.kanban.kanbanProject.enums.BoardRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardMemberDTO {
    private Long userId;
    private String firstName;
    private String lastName;
    private String emailId;
    private BoardRole role;
}
