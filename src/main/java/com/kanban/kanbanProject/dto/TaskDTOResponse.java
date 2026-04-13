package com.kanban.kanbanProject.dto;

import com.kanban.kanbanProject.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskDTOResponse {
    private Long id;
    private String title;
    private String content;
    private TaskStatus status;
    private LocalDateTime createdOn;
    private LocalDateTime updatedOn;
}