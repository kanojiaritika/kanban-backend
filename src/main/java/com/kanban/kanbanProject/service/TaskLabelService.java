package com.kanban.kanbanProject.service;

import com.kanban.kanbanProject.dto.LabelDTO;
import com.kanban.kanbanProject.entity.*;
import com.kanban.kanbanProject.exceptions.KanbanException;
import com.kanban.kanbanProject.repository.BoardMembersRepo;
import com.kanban.kanbanProject.repository.LabelRepo;
import com.kanban.kanbanProject.repository.TaskLabelRepo;
import com.kanban.kanbanProject.repository.TasksRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
                .orElseThrow(() -> new KanbanException("Task not found", HttpStatus.NOT_FOUND));

        Boards board = task.getColumn().getBoard();

        boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new KanbanException("Not a board member. Access Denied.", HttpStatus.FORBIDDEN));

        Labels label = labelRepo.findById(labelId)
                .orElseThrow(() -> new KanbanException("Label not found", HttpStatus.NOT_FOUND));

        // Prevent duplicate assignment
        boolean alreadyAssigned = taskLabelRepo.existsByTaskAndLabel(task, label);
        if (alreadyAssigned) {
            throw new KanbanException("Label already assigned to this task", HttpStatus.BAD_REQUEST);
        }

        TaskLabels taskLabel = new TaskLabels();
        taskLabel.setTask(task);
        taskLabel.setLabel(label);

        taskLabelRepo.save(taskLabel);
    }

    // Unassign label from task
    public void unassignLabel(Long taskId, Long labelId, Users user) {
        Tasks task = tasksRepo.findById(taskId)
                .orElseThrow(() -> new KanbanException("Task not found", HttpStatus.NOT_FOUND));

        Boards board = task.getColumn().getBoard();

        boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new KanbanException("Not a board member. Access Denied.", HttpStatus.FORBIDDEN));

        Labels label = labelRepo.findById(labelId)
                .orElseThrow(() -> new KanbanException("Label not found", HttpStatus.NOT_FOUND));

        TaskLabels taskLabel = taskLabelRepo.findByTaskAndLabel(task, label)
                .orElseThrow(() -> new KanbanException("Label not assigned to this task", HttpStatus.BAD_REQUEST));

        taskLabelRepo.delete(taskLabel);
    }

    // Get all labels on a task
    public List<LabelDTO> getLabelsByTask(Long taskId, Users user) {
        Tasks task = tasksRepo.findById(taskId)
                .orElseThrow(() -> new KanbanException("Task not found", HttpStatus.NOT_FOUND));

        Boards board = task.getColumn().getBoard();

        boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new KanbanException("Not a board member. Access Denied.", HttpStatus.FORBIDDEN));

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