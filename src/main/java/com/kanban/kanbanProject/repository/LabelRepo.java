package com.kanban.kanbanProject.repository;

import com.kanban.kanbanProject.entity.Labels;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LabelRepo extends JpaRepository<Labels, Long> {

    List<Labels> findByBoardId(Long boardId);
}
