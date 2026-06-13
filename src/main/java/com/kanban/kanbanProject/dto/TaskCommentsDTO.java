package com.kanban.kanbanProject.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskCommentsDTO {
    private Long id;
    private String comment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
