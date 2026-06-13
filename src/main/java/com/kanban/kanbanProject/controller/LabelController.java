package com.kanban.kanbanProject.controller;

import com.kanban.kanbanProject.dto.LabelDTO;
import com.kanban.kanbanProject.entity.Users;
import com.kanban.kanbanProject.service.LabelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards/{boardId}/labels")
public class LabelController {

    @Autowired
    private LabelService labelService;

    @PostMapping
    public ResponseEntity<String> createLabel(
            @PathVariable Long boardId,
            @RequestBody LabelDTO dto,
            @AuthenticationPrincipal Users user) {
        labelService.createLabel(boardId, dto, user);
        return ResponseEntity.ok("Label created successfully");
    }

    @GetMapping
    public ResponseEntity<List<LabelDTO>> getLabels(
            @PathVariable Long boardId,
            @AuthenticationPrincipal Users user) {
        return ResponseEntity.ok(labelService.getLabelsByBoard(boardId, user));
    }

    @PatchMapping("/{labelId}")
    public ResponseEntity<String> updateLabel(
            @PathVariable Long boardId,
            @PathVariable Long labelId,
            @RequestBody LabelDTO dto,
            @AuthenticationPrincipal Users user) {
        labelService.updateLabel(boardId, labelId, dto, user);
        return ResponseEntity.ok("Label updated successfully");
    }

    @DeleteMapping("/{labelId}")
    public ResponseEntity<String> deleteLabel(
            @PathVariable Long boardId,
            @PathVariable Long labelId,
            @AuthenticationPrincipal Users user) {
        labelService.deleteLabel(boardId, labelId, user);
        return ResponseEntity.ok("Label deleted successfully");
    }
}