package com.kanban.kanbanProject.controller;

import com.kanban.kanbanProject.dto.ColumnDTO;
import com.kanban.kanbanProject.entity.Columns;
import com.kanban.kanbanProject.entity.Users;
import com.kanban.kanbanProject.service.ColumnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/columns")
public class ColumnsController {

    @Autowired
    private ColumnsService columnsService;

    @PostMapping("/{boardId}")
    public ResponseEntity<Columns> createColumn(@PathVariable Long boardId,
                                                @RequestBody ColumnDTO columnDTO,
                                                @AuthenticationPrincipal Users user) {
        Columns column = columnsService.createColumn(boardId, columnDTO, user);
        return ResponseEntity.ok(column);
    }

    @PutMapping("/{columnId}")
    public void updateColumn(@PathVariable Long columnId,
                             @RequestBody ColumnDTO columnDTO,
                             @AuthenticationPrincipal Users user) {
        columnsService.updateColumn(columnId, columnDTO, user);
    }

    @GetMapping("/{columnId}")
    public ResponseEntity<ColumnDTO> getColumnById(@PathVariable Long columnId,
                                                   @AuthenticationPrincipal Users user) {
        return columnsService.getColumnById(columnId, user);
    }

    @GetMapping("/all/{boardId}")
    public ResponseEntity<List<ColumnDTO>> getAllColumns(@PathVariable Long boardId,
                                                         @AuthenticationPrincipal Users user) {
        return columnsService.getAllColumns(boardId, user);
    }

    @DeleteMapping("/{columnId}")
    public ResponseEntity<String> deleteColumn(@PathVariable Long columnId,
                                               @AuthenticationPrincipal Users user) {
        columnsService.deleteColumn(columnId, user);
        return ResponseEntity.ok("Column deleted successfully");
    }
}
