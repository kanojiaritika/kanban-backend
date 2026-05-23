package com.kanban.kanbanProject.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
public class Boards {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private LocalDateTime createdOn;

    @ManyToMany(mappedBy = "boards")
    private Set<Users> users = new HashSet<>();
    // mappedBy = "boards" tells Hibernate that this relationship
    // is already mapped and managed by the 'boards' field
    // inside the Users entity.
    //
    // So Hibernate:
    // - does not create another join table
    // - does not treat this as a separate relationship
    // - reuses the mapping defined on the owning side

    @OneToMany(mappedBy = "board")
    private Set<Columns> columns;


}
