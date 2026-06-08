package com.kanban.kanbanProject.repository;

import com.kanban.kanbanProject.entity.BoardMembers;
import com.kanban.kanbanProject.entity.Boards;
import com.kanban.kanbanProject.entity.Users;
import com.kanban.kanbanProject.enums.BoardRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BoardMembersRepo extends JpaRepository<BoardMembers, Long> {

    // Find a specific user's membership in a specific board
    Optional<BoardMembers> findByBoardAndUser(Boards board, Users user);

    // Find all members of a board
    List<BoardMembers> findByBoard(Boards board);

    // Find all boards a user is a member of
    List<BoardMembers> findByUser(Users user);

    // Check if user has a specific role on a board
    Optional<BoardMembers> findByBoardAndUserAndRole(Boards board, Users user, BoardRole role);
}