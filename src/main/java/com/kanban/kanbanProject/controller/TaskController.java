package com.kanban.kanbanProject.controller;

import com.kanban.kanbanProject.dto.TaskDTO;
import com.kanban.kanbanProject.entity.Users;
import com.kanban.kanbanProject.service.TaskService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @PostMapping("/{columnId}")
    public void createTask(@PathVariable Long columnId,
                           @RequestBody TaskDTO taskDTO,
                           @AuthenticationPrincipal Users user) {
        taskService.createTask(columnId, taskDTO, user);
    }

    @PutMapping("/{taskId}")
    public void updateTask(@PathVariable Long taskId,
                           @RequestBody TaskDTO taskDTO,
                           @AuthenticationPrincipal Users user) {
        taskService.updateTask(taskId, taskDTO, user);
    }

    @GetMapping("/{taskId}")
    public TaskDTO getTaskById(@PathVariable Long taskId,
                            @AuthenticationPrincipal Users user) {
        return taskService.getTaskById(taskId, user);
    }

    @GetMapping
    public List<TaskDTO> getAllTasks(@AuthenticationPrincipal Users user) {
        return taskService.getAllTasksForUser(user);
    }

    @DeleteMapping("/{taskId}")
    public void deleteTaskById(@PathVariable Long taskId, @AuthenticationPrincipal Users user) {
        taskService.deleteTask(taskId, user);
    }
}
