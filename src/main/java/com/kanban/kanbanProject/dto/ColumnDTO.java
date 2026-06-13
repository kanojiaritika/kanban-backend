package com.kanban.kanbanProject.dto;

import com.kanban.kanbanProject.entity.Boards;
import com.kanban.kanbanProject.entity.Tasks;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Data
public class ColumnDTO {

    private Long id;
    private String name;
    private Integer position;
    private LocalDateTime createdAt;
    private List<TaskDTO> taskDTOS;
}
