package com.kanban.kanbanProject.repository;

import com.kanban.kanbanProject.entity.Boards;
import com.kanban.kanbanProject.entity.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Repository
public interface TasksRepo extends JpaRepository<Tasks, Long> {

    List<Tasks> findByColumnId(Long columnId);

    List<Tasks> findByColumnBoardIn(List<Boards> boards);

}
