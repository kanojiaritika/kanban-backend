package com.kanban.kanbanProject.controller;

import com.kanban.kanbanProject.dto.BoardDTO;
import com.kanban.kanbanProject.entity.BoardMembers;
import com.kanban.kanbanProject.entity.Boards;
import com.kanban.kanbanProject.entity.Users;
import com.kanban.kanbanProject.enums.BoardRole;
import com.kanban.kanbanProject.service.BoardsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/boards")
public class BoardsController {

    @Autowired
    private BoardsService boardsService;

    @PostMapping
    public ResponseEntity<Boards> createBoard(@RequestBody BoardDTO boardDTO,
                                              @AuthenticationPrincipal Users user) {
        return ResponseEntity.ok(boardsService.createBoard(boardDTO, user));
    }

    @GetMapping
    public ResponseEntity<List<Boards>> getMyBoards(@AuthenticationPrincipal Users user) {
        return ResponseEntity.ok(boardsService.getMyBoards(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Boards> getBoard(@PathVariable Long id,
                                           @AuthenticationPrincipal Users user) {
        return ResponseEntity.ok(boardsService.getBoard(id, user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boards> updateBoard(@PathVariable Long id,
                                              @RequestParam String title,
                                              @AuthenticationPrincipal Users user) {
        return ResponseEntity.ok(boardsService.updateBoard(id, title, user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBoard(@PathVariable Long id,
                                            @AuthenticationPrincipal Users user) {
        boardsService.deleteBoard(id, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/members")
    public ResponseEntity<BoardMembers> addMember(@PathVariable Long id,
                                                  @RequestParam Long userId,
                                                  @RequestParam BoardRole role,
                                                  @AuthenticationPrincipal Users requestingUser) {
        return ResponseEntity.ok(boardsService.addMember(id, userId, role, requestingUser));
    }
}