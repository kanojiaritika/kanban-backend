package com.kanban.kanbanProject.repository;

import com.kanban.kanbanProject.entity.Boards;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BoardsRepo extends JpaRepository<Boards, Long> {

    List<Boards> getAllBoardsByUsersId(Long id);

    Boards findByUsersIdAndId(Long userId, Long Id);

}
