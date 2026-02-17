package com.kanban.kanbanProject.entity;

import com.kanban.kanbanProject.TaskStatus;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class Tasks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String content;

    private LocalDateTime createdOn;

    private LocalDateTime updatedOn;

    private TaskStatus status;

    @ManyToOne
    @JoinColumn(name = "boards_id", nullable = false)
    private Boards boards;


}
