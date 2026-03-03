package com.kanban.kanbanProject.dto;

import com.kanban.kanbanProject.entity.Tasks;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class BoardDTO {

    private String title;
    private LocalDateTime createdAt;
    private List<Tasks> tasks;

}
