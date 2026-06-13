package com.kanban.kanbanProject.controller;

import com.kanban.kanbanProject.dto.LabelDTO;
import com.kanban.kanbanProject.entity.Users;
import com.kanban.kanbanProject.service.TaskLabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks/{taskId}/labels")
public class TaskLabelController {

    @Autowired
    private TaskLabelService taskLabelService;

    @PostMapping("/{labelId}")
    public ResponseEntity<String> assignLabel(
            @PathVariable Long taskId,
            @PathVariable Long labelId,
            @AuthenticationPrincipal Users user) {
        taskLabelService.assignLabel(taskId, labelId, user);
        return ResponseEntity.ok("Label assigned successfully");
    }

    @DeleteMapping("/{labelId}")
    public ResponseEntity<String> unassignLabel(
            @PathVariable Long taskId,
            @PathVariable Long labelId,
            @AuthenticationPrincipal Users user) {
        taskLabelService.unassignLabel(taskId, labelId, user);
        return ResponseEntity.ok("Label unassigned successfully");
    }

    @GetMapping
    public ResponseEntity<List<LabelDTO>> getLabels(
            @PathVariable Long taskId,
            @AuthenticationPrincipal Users user) {
        return ResponseEntity.ok(taskLabelService.getLabelsByTask(taskId, user));
    }
}
