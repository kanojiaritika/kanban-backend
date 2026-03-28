package com.kanban.kanbanProject.repository;

import com.kanban.kanbanProject.entity.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TasksRepo extends JpaRepository<Tasks, Long> {
    Optional<Tasks> findByIdAndBoardsId(Long taskId, Long boardId);
}
