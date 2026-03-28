package com.kanban.kanbanProject.controller;

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
}
