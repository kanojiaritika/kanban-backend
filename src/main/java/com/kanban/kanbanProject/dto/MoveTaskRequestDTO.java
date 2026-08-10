package com.kanban.kanbanProject.dto;

import lombok.Data;

@Data
public class MoveTaskRequestDTO {
    private Long newColumnId;
    private Integer newPosition; // 0-indexed target position within newColumnId
}
