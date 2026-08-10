package com.kanban.kanbanProject.dto;

import com.kanban.kanbanProject.enums.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TaskDTO {

    private Long id;

    private String title;

    private String content;

    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;

    private TaskStatus status;

    private UserDTO userDTO;
}
