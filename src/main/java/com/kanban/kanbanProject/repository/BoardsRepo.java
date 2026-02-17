package com.kanban.kanbanProject.repository;

import com.kanban.kanbanProject.entity.Boards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardsRepo extends JpaRepository<Boards, Long> {
}
