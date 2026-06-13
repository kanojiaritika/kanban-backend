package com.kanban.kanbanProject.repository;

import com.kanban.kanbanProject.entity.TaskComments;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Arrays;
import java.util.List;

public interface TaskCommentRepo extends JpaRepository<TaskComments, Long> {
    List<TaskComments> findByTaskId(Long taskId);

}
