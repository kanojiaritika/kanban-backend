package com.kanban.kanbanProject.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BoardDTO {

    private Long id;
    private String title;
    private LocalDateTime createdAt;
    private List<TaskDTO> tasks;

}
