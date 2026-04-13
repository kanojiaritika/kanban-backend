package com.kanban.kanbanProject.entity;

import com.kanban.kanbanProject.enums.BoardRole;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(uniqueConstraints = {@UniqueConstraint(columnNames = {"board_id", "user_id"})})
public class BoardMembers {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private BoardRole role;

    private Boolean isActive;

    private LocalDateTime joinedAt;

    private LocalDateTime updatedAt;

    // MANY members can belong to ONE board
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Boards board;

    // MANY board entries can belong to ONE user
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Users user;

}
