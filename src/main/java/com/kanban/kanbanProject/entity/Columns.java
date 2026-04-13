package com.kanban.kanbanProject.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Columns {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long position;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Boards board;
}
