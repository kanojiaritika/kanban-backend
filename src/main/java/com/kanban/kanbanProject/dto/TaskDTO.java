package com.kanban.kanbanProject.dto;

import com.kanban.kanbanProject.TaskStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskDTO {

    private String title;

    private String content;
}
