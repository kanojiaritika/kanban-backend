package com.kanban.kanbanProject.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class TaskLabels {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id")
    private Tasks task;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "label_id")
    private Labels label;
}
