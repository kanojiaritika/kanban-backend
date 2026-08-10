package com.kanban.kanbanProject.repository;

import com.kanban.kanbanProject.entity.Boards;
import com.kanban.kanbanProject.entity.Columns;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ColumnsRepo extends JpaRepository<Columns, Long> {

    Optional<Columns> findByBoardIdAndPosition(Long boardId, Integer position);
    List<Columns> findByBoardIdOrderByPositionAsc(Long boardId);
    List<Columns> findByBoardId(Long boardId);

    @Query("SELECT MAX(c.position) FROM Columns c WHERE c.board = :board")
    Integer findMaxPositionByBoard(@Param("board") Boards board);

    void deleteAllByBoard(Boards board);
}
