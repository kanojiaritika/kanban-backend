package com.kanban.kanbanProject.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
public class Columns {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer position;

    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "boards_id", referencedColumnName = "id")
    private Boards board;
}
