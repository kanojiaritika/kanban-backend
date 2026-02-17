package com.kanban.kanbanProject.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
public class Boards {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private LocalDateTime createdOn;

    @OneToMany(mappedBy = "boards", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Tasks> tasks;

    @ManyToOne
    @JoinColumn(name = "users_id", nullable = false)
    private Users users;


}
