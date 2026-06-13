package com.kanban.kanbanProject.service;

import com.kanban.kanbanProject.dto.LabelDTO;
import com.kanban.kanbanProject.entity.*;
import com.kanban.kanbanProject.repository.BoardMembersRepo;
import com.kanban.kanbanProject.repository.LabelRepo;
import com.kanban.kanbanProject.repository.TaskLabelRepo;
import com.kanban.kanbanProject.repository.TasksRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskLabelService {

    @Autowired
    private TasksRepo tasksRepo;

    @Autowired
    private LabelRepo labelRepo;

    @Autowired
    private TaskLabelRepo taskLabelRepo;

    @Autowired
    private BoardMembersRepo boardMembersRepo;

    // Assign label to task
    public void assignLabel(Long taskId, Long labelId, Users user) {
        Tasks task = tasksRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Boards board = task.getColumn().getBoard();

        boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new RuntimeException("Not a board member. Access denied"));

        Labels label = labelRepo.findById(labelId)
                .orElseThrow(() -> new RuntimeException("Label not found"));

        // Prevent duplicate assignment
        boolean alreadyAssigned = taskLabelRepo.existsByTaskAndLabel(task, label);
        if (alreadyAssigned) {
            throw new RuntimeException("Label already assigned to this task");
        }

        TaskLabels taskLabel = new TaskLabels();
        taskLabel.setTask(task);
        taskLabel.setLabel(label);

        taskLabelRepo.save(taskLabel);
    }

    // Unassign label from task
    public void unassignLabel(Long taskId, Long labelId, Users user) {
        Tasks task = tasksRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Boards board = task.getColumn().getBoard();

        boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new RuntimeException("Not a board member. Access denied"));

        Labels label = labelRepo.findById(labelId)
                .orElseThrow(() -> new RuntimeException("Label not found"));

        TaskLabels taskLabel = taskLabelRepo.findByTaskAndLabel(task, label)
                .orElseThrow(() -> new RuntimeException("Label not assigned to this task"));

        taskLabelRepo.delete(taskLabel);
    }

    // Get all labels on a task
    public List<LabelDTO> getLabelsByTask(Long taskId, Users user) {
        Tasks task = tasksRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Boards board = task.getColumn().getBoard();

        boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new RuntimeException("Not a board member. Access denied"));

        return taskLabelRepo.findByTask(task)
                .stream()
                .map(tl -> {
                    LabelDTO dto = new LabelDTO();
                    dto.setId(tl.getLabel().getId());
                    dto.setName(tl.getLabel().getName());
                    dto.setColor(tl.getLabel().getColor());
                    return dto;
                })
                .toList();
    }
}