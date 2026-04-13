package com.kanban.kanbanProject.entity;

import com.kanban.kanbanProject.enums.TaskAction;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class TaskActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String oldValue;

    private String newValue;

    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    private TaskAction action;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Tasks task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;
}
