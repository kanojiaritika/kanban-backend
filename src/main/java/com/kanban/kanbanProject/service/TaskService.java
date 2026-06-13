package com.kanban.kanbanProject.service;

import com.kanban.kanbanProject.dto.TaskDTO;
import com.kanban.kanbanProject.entity.*;
import com.kanban.kanbanProject.enums.BoardRole;
import com.kanban.kanbanProject.repository.BoardMembersRepo;
import com.kanban.kanbanProject.repository.ColumnsRepo;
import com.kanban.kanbanProject.repository.TasksRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TasksRepo tasksRepo;

    @Autowired
    private ColumnsRepo columnsRepo;

    @Autowired
    private BoardMembersRepo boardMembersRepo;

    // Create Task
    public void createTask(Long columnId, TaskDTO taskDTO, Users user) {

        // Fetch column (which gives us board too)
        Columns column = columnsRepo.findById(columnId)
                .orElseThrow(() -> new RuntimeException("Column not found"));

        Boards board = column.getBoard();

        // Check if user is a member of this board
        BoardMembers member = boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new RuntimeException("Not a board member. Access denied"));

        Tasks task = new Tasks();
        task.setTitle(taskDTO.getTitle());
        task.setContent(taskDTO.getContent());
        task.setStatus(taskDTO.getStatus());
        task.setCreatedOn(LocalDateTime.now());
        task.setCreatedBy(user);
        task.setColumn(column);

        tasksRepo.save(task);
    }

    // Update Task
    public void updateTask(Long taskId, TaskDTO taskDTO, Users user) {
        Tasks task = tasksRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Boards board = task.getColumn().getBoard();

        boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new RuntimeException("Not a board member. Access denied"));

        if (taskDTO.getTitle() != null) task.setTitle(taskDTO.getTitle());
        if (taskDTO.getContent() != null) task.setContent(taskDTO.getContent());
        if (taskDTO.getStatus() != null) task.setStatus(taskDTO.getStatus());

        task.setUpdatedOn(LocalDateTime.now());

        tasksRepo.save(task);
    }

    // Get all tasks for a user
    public List<TaskDTO> getAllTasksForUser(Users user) {
        List<BoardMembers> memberships = boardMembersRepo.findByUser(user);
        List<Boards> boards = memberships.stream()
                .map(BoardMembers::getBoard)
                .toList();

        return tasksRepo.findByColumnBoardIn(boards)
                .stream()
                .map(this::toDTO)

                .toList();
    }

    // Get task by ID
    public TaskDTO getTaskById(Long taskId, Users user) {
        Tasks task = tasksRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Boards board = task.getColumn().getBoard();

        boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new RuntimeException("Not a board member. Access denied"));

        return toDTO(task);
    }

    // Delete task by ID
    public void deleteTask(Long taskId, Users user) {

        Tasks task = tasksRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Boards board = task.getColumn().getBoard();

        BoardMembers member = boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new RuntimeException("Not a board member. Access denied"));

        // Check if user has created the task or is ADMIN or OWNER
        boolean isCreator = task.getCreatedBy().getId().equals(user.getId());
        boolean isAdminOrOwner = member.getRole().equals(BoardRole.ADMIN)
                || member.getRole().equals(BoardRole.OWNER);
        if (!isCreator && !isAdminOrOwner) {
            throw new RuntimeException("Not authorized to delete");
        }

        BoardRole role = member.getRole();
        if (!role.equals(BoardRole.OWNER) && !role.equals(BoardRole.ADMIN)) {
            throw new RuntimeException("Only Owner or Admin can delete tasks");
        }

        tasksRepo.delete(task);
    }

    // Mapper
    private TaskDTO toDTO(Tasks task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setContent(task.getContent());
        dto.setStatus(task.getStatus());
        dto.setCreatedOn(task.getCreatedOn());
        dto.setUpdatedOn(task.getUpdatedOn());
        return dto;
    }
}
