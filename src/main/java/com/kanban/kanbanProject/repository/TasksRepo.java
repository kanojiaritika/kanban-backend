package com.kanban.kanbanProject.repository;

import com.kanban.kanbanProject.entity.Boards;
import com.kanban.kanbanProject.entity.Columns;
import com.kanban.kanbanProject.entity.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TasksRepo extends JpaRepository<Tasks, Long> {

    List<Tasks> findByColumnIdOrderByPositionAsc(Long columnId);

    List<Tasks> findByColumnBoardIn(List<Boards> boards);

    void deleteAllByColumn(Columns column);

    @Query("SELECT COALESCE(MAX(t.position), -1) FROM Tasks t WHERE t.column.id = :columnId")
    Integer findMaxPositionInColumn(@Param("columnId") Long columnId);

    // Close the gap in the OLD column after a task leaves it
    @Modifying
    @Query("UPDATE Tasks t SET t.position = t.position - 1 " +
            "WHERE t.column.id = :columnId AND t.position > :removedPosition")
    void decrementPositionsAfter(@Param("columnId") Long columnId, @Param("removedPosition") Integer removedPosition);

    // Make room in the NEW column before inserting at newPosition
    @Modifying
    @Query("UPDATE Tasks t SET t.position = t.position + 1 " +
            "WHERE t.column.id = :columnId AND t.position >= :fromPosition")
    void incrementPositionsFrom(@Param("columnId") Long columnId, @Param("fromPosition") Integer fromPosition);
}
