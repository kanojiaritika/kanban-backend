package com.kanban.kanbanProject.repository;

import com.kanban.kanbanProject.entity.Columns;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ColumnsRepo extends JpaRepository<Columns, Long> {

    Optional<Columns> findByBoardIdAndPosition(Long boardId, Integer position);
    List<Columns> findByBoardIdOrderByPositionAsc(Long boardId);
    List<Columns> findByBoardId(Long boardId);
}
