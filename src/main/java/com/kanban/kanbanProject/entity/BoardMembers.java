package com.kanban.kanbanProject.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class BoardMembers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // MANY members can belong to ONE board
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Boards board;

    // MANY board entries can belong to ONE user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

    // extra fields (VERY IMPORTANT)
    private String role; // ADMIN, MEMBER

    private Boolean isActive;

    private LocalDateTime joinedAt;
}
