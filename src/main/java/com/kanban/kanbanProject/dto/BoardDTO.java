package com.kanban.kanbanProject.dto;

import com.kanban.kanbanProject.enums.BoardRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardDTO {

    private Long id;
    private String title;
    private String description;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
    private List<BoardMemberDTO> members;
    private BoardRole userRole;
    private Boolean isFavorite;
    private Boolean isArchived;

}
