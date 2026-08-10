package com.kanban.kanbanProject.controller;

import com.kanban.kanbanProject.dto.MoveTaskRequestDTO;
import com.kanban.kanbanProject.dto.TaskDTO;
import com.kanban.kanbanProject.entity.Users;
import com.kanban.kanbanProject.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/{columnId}")
    public ResponseEntity<TaskDTO> createTask(@PathVariable Long columnId,
                                              @RequestBody TaskDTO taskDTO,
                                              @AuthenticationPrincipal Users user) {
        TaskDTO created = taskService.createTask(columnId, taskDTO, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long taskId,
                                              @RequestBody TaskDTO taskDTO,
                                              @AuthenticationPrincipal Users user) {
        TaskDTO updated = taskService.updateTask(taskId, taskDTO, user);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{taskId}")
    public ResponseEntity<TaskDTO> getTaskById(@PathVariable Long taskId,
                                               @AuthenticationPrincipal Users user) {
        return ResponseEntity.ok(taskService.getTaskById(taskId, user));
    }

    @GetMapping
    public ResponseEntity<List<TaskDTO>> getAllTasks(@AuthenticationPrincipal Users user) {
        return ResponseEntity.ok(taskService.getAllTasksForUser(user));
    }

    @GetMapping("/column/{columnId}")
    public ResponseEntity<List<TaskDTO>> getTasksByColumnId(@PathVariable Long columnId,
                                                            @AuthenticationPrincipal Users user) {
        return ResponseEntity.ok(taskService.getTasksByColumnId(columnId, user));
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<Void> deleteTaskById(@PathVariable Long taskId,
                                               @AuthenticationPrincipal Users user) {
        taskService.deleteTask(taskId, user);
        return ResponseEntity.noContent().build();
    }

    // Assign / reassign a task to a board member
    @PutMapping("/{taskId}/assignee")
    public ResponseEntity<TaskDTO> assignTask(@PathVariable Long taskId,
                                              @RequestBody Map<String, String> body,
                                              @AuthenticationPrincipal Users user) {
        TaskDTO updated = taskService.assignTask(taskId, body.get("emailId"), user);
        return ResponseEntity.ok(updated);
    }

    @PatchMapping("/{taskId}/move")
    public ResponseEntity<TaskDTO> moveTask(@PathVariable Long taskId,
                                            @RequestBody MoveTaskRequestDTO request,
                                            @AuthenticationPrincipal Users user) {
        TaskDTO updated = taskService.moveTask(taskId, request.getNewColumnId(), request.getNewPosition(), user);
        return ResponseEntity.ok(updated);
    }

    // Unassign the current assignee from a task
    @DeleteMapping("/{taskId}/assignee")
    public ResponseEntity<TaskDTO> removeAssignee(@PathVariable Long taskId,
                                                  @RequestParam String emailId,
                                                  @AuthenticationPrincipal Users user) {
        TaskDTO updated = taskService.removeMemberFromTask(taskId, emailId, user);
        return ResponseEntity.ok(updated);
    }
}