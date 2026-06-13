package com.kanban.kanbanProject.controller;

import com.kanban.kanbanProject.dto.TaskCommentsDTO;
import com.kanban.kanbanProject.entity.Users;
import com.kanban.kanbanProject.service.TaskCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks/{taskId}/comments")
public class TaskCommentController {

    @Autowired
    private TaskCommentService taskCommentService;

    @PostMapping
    public ResponseEntity<String> createComment(
            @PathVariable Long taskId,
            @RequestBody TaskCommentsDTO dto,
            @AuthenticationPrincipal Users user) {
        taskCommentService.createComment(taskId, dto, user);
        return ResponseEntity.ok("Comment added successfully");
    }

    @GetMapping
    public ResponseEntity<List<TaskCommentsDTO>> getComments(
            @PathVariable Long taskId,
            @AuthenticationPrincipal Users user) {
        return ResponseEntity.ok(taskCommentService.getCommentsByTaskId(taskId, user));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<String> updateComment(
            @PathVariable Long taskId,
            @PathVariable Long commentId,
            @RequestBody TaskCommentsDTO dto,
            @AuthenticationPrincipal Users user) {
        taskCommentService.updateComment(commentId, dto, user);
        return ResponseEntity.ok("Comment updated successfully");
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<String> deleteComment(
            @PathVariable Long taskId,
            @PathVariable Long commentId,
            @AuthenticationPrincipal Users user) {
        taskCommentService.deleteComment(commentId, user);
        return ResponseEntity.ok("Comment deleted successfully");
    }
}