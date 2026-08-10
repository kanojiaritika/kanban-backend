package com.kanban.kanbanProject.repository;

import com.kanban.kanbanProject.entity.Boards;
import com.kanban.kanbanProject.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardsRepo extends JpaRepository<Boards, Long> {

    @Query("""
        SELECT bm.board
        FROM BoardMembers bm
        WHERE bm.user = :currentUser
          AND bm.board.createdby <> :currentUser
    """)
    List<Boards> findSharedBoards(@Param("currentUser") Users currentUser);

    List<Boards> findByIsArchived(Boolean isArchived);



}
