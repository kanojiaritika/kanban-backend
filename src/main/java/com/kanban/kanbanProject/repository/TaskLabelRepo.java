package com.kanban.kanbanProject.repository;

import com.kanban.kanbanProject.entity.Labels;
import com.kanban.kanbanProject.entity.TaskLabels;
import com.kanban.kanbanProject.entity.Tasks;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskLabelRepo extends JpaRepository<TaskLabels, Long> {
    boolean existsByTaskAndLabel(Tasks task, Labels label);

    Optional<TaskLabels> findByTaskAndLabel(Tasks task, Labels label);

    List<TaskLabels> findByTask(Tasks task);
}
