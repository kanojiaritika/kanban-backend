package com.kanban.kanbanProject.service;

import com.kanban.kanbanProject.dto.TaskCommentsDTO;
import com.kanban.kanbanProject.entity.*;
import com.kanban.kanbanProject.enums.BoardRole;
import com.kanban.kanbanProject.repository.BoardMembersRepo;
import com.kanban.kanbanProject.repository.TaskCommentRepo;
import com.kanban.kanbanProject.repository.TasksRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskCommentService {

    @Autowired
    private TasksRepo tasksRepo;

    @Autowired
    private TaskCommentRepo taskCommentRepo;

    @Autowired
    private BoardMembersRepo boardMembersRepo;

    // Create Task Comment
    public void createComment(Long taskId, TaskCommentsDTO dto, Users user) {
        Tasks task = tasksRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Boards board = task.getColumn().getBoard();

        BoardMembers member = boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new RuntimeException("Not a board member. Access denied"));

        TaskComments taskComments = new TaskComments();
        taskComments.setComment(dto.getComment());
        taskComments.setCreatedAt(LocalDateTime.now());
        taskComments.setUpdatedAt(LocalDateTime.now());
        taskComments.setUser(user);
        taskComments.setTask(task);
        taskCommentRepo.save(taskComments);
    }

    // Get all comments for a task
    public List<TaskCommentsDTO> getCommentsByTaskId(Long taskId, Users user) {
        Tasks task = tasksRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        Boards board = task.getColumn().getBoard();

        boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new RuntimeException("Not a board member. Access denied"));

        return taskCommentRepo.findByTaskId(taskId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // Update comment - only creator can update
    public void updateComment(Long commentId, TaskCommentsDTO dto, Users user) {
        TaskComments comment = taskCommentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Only the comment author can edit this comment");
        }

        if (dto.getComment() != null) comment.setComment(dto.getComment());
        comment.setUpdatedAt(LocalDateTime.now());

        taskCommentRepo.save(comment);
    }

    // Delete comment - creator, admin, or owner can delete
    public void deleteComment(Long commentId, Users user) {
        TaskComments comment = taskCommentRepo.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        Boards board = comment.getTask().getColumn().getBoard();

        BoardMembers member = boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new RuntimeException("Not a board member. Access denied"));

        boolean isAuthor = comment.getUser().getId().equals(user.getId());
        boolean isAdminOrOwner = member.getRole().equals(BoardRole.ADMIN)
                || member.getRole().equals(BoardRole.OWNER);

        if (!isAuthor && !isAdminOrOwner) {
            throw new RuntimeException("Not authorized to delete this comment");
        }

        taskCommentRepo.delete(comment);
    }

    // Mapper
    private TaskCommentsDTO toDTO(TaskComments comment) {
        TaskCommentsDTO dto = new TaskCommentsDTO();
        dto.setId(comment.getId());
        dto.setComment(comment.getComment());
        dto.setCreatedAt(comment.getCreatedAt());
        dto.setUpdatedAt(comment.getUpdatedAt());
        dto.setAuthorName(comment.getUser().getUsername()); // so frontend knows who wrote it
        return dto;
    }

}
