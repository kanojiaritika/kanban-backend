package com.kanban.kanbanProject.controller;

import com.kanban.kanbanProject.TaskStatus;
import com.kanban.kanbanProject.dto.TaskDTO;
import com.kanban.kanbanProject.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/tasks/{boardId}")
    public ResponseEntity<?> createTask(@PathVariable Long boardId,
                                        @RequestBody TaskDTO taskDTO) {
        return taskService.createTask(boardId, taskDTO);
    }

    @PutMapping("/tasks/{boardId}/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable Long taskId,
                                        @PathVariable Long boardId,
                                        @RequestBody TaskDTO taskDTO) {
        return taskService.updateTask(taskId, boardId, taskDTO);
    }

    @GetMapping("/tasks/{boardId}")
    public ResponseEntity<?> getTasks(@PathVariable Long boardId) {
        return taskService.getAllTasks(boardId);
    }

    @GetMapping("/tasks/{boardId}/{taskId}")
    public ResponseEntity<?> getTask(@PathVariable Long boardId,  @PathVariable Long taskId) {

        return taskService.getTaskById(boardId, taskId);
    }

    @DeleteMapping("/tasks/{boardId}/{taskId}")
    public void deleteTask(@PathVariable Long boardId,  @PathVariable Long taskId) {
        taskService.deleteTask(boardId, taskId);
    }

    @PutMapping("/tasks/status/{boardId}/{taskId}")
    public void updateStatus(@PathVariable Long boardId,  @PathVariable Long taskId, @RequestParam TaskStatus status) {
        taskService.updateTaskStatus(boardId, taskId, status);
    }
}
