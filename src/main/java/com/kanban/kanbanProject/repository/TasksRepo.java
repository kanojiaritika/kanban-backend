package com.kanban.kanbanProject.repository;

import com.kanban.kanbanProject.entity.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TasksRepo extends JpaRepository<Tasks, Long> {
}
