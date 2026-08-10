package com.kanban.kanbanProject.controller;

import com.kanban.kanbanProject.dto.BoardDTO;
import com.kanban.kanbanProject.dto.BoardMemberDTO;
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
    public ResponseEntity<List<BoardDTO>> getMyBoards(@AuthenticationPrincipal Users user) {
        return ResponseEntity.ok(boardsService.getMyBoards(user));
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardDTO> getBoard(@PathVariable Long id,
                                           @AuthenticationPrincipal Users user) {
        return ResponseEntity.ok(boardsService.getBoard(id, user));
    }

    @GetMapping("/sharedBoards")
    public ResponseEntity<List<BoardDTO>> getSharedBoards(@AuthenticationPrincipal Users user) {
        return ResponseEntity.ok(boardsService.getSharedBoards(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Boards> updateBoard(@PathVariable Long id,
                                              @RequestBody BoardDTO boardDTO,
                                              @AuthenticationPrincipal Users user) {
        return ResponseEntity.ok(boardsService.updateBoard(id, boardDTO, user));
    }

    @PutMapping("/favorite/{boardId}")
    public ResponseEntity<BoardDTO> makeBoardFavorite(
            @PathVariable Long boardId,
            @AuthenticationPrincipal Users currentUser) {
        return ResponseEntity.ok(boardsService.makeBoardFavorite(boardId, currentUser));
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<BoardDTO>> getFavBoards(@AuthenticationPrincipal Users user) {
        return ResponseEntity.ok(boardsService.getFavoriteBoards(user));
    }

    // Archive board
    @PutMapping("/{boardId}/archive")
    public ResponseEntity<Void> archiveBoard(
            @PathVariable Long boardId,
            @AuthenticationPrincipal Users currentUser) {

        boardsService.archiveBoard(boardId, currentUser);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/archived")
    public ResponseEntity<List<BoardDTO>> getArchivedBoards(@AuthenticationPrincipal Users currentUser) {

        return ResponseEntity.ok(boardsService.getArchivedBoards(currentUser));
    }

    @PutMapping("/{boardId}/unarchive")
    public ResponseEntity<Void> unarchiveBoard(
            @PathVariable Long boardId,
            @AuthenticationPrincipal Users currentUser) {

        boardsService.unarchiveBoard(boardId, currentUser);

        return ResponseEntity.ok().build();
    }

    // Get recently opened boards
    @GetMapping("/recent")
    public ResponseEntity<List<BoardDTO>> getRecentlyOpened(
            @AuthenticationPrincipal Users currentUser) {

        return ResponseEntity.ok(boardsService.getRecentlyOpened(currentUser));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBoard(@PathVariable Long id,
                                            @AuthenticationPrincipal Users user) {
        boardsService.deleteBoard(id, user);
        return ResponseEntity.ok("Board deleted");
    }

    @PostMapping("/member/{id}")
    public ResponseEntity<BoardMembers> addMember(@PathVariable Long id,
                                                  @RequestParam String emailId,
                                                  @RequestParam BoardRole role,
                                                  @AuthenticationPrincipal Users requestingUser) {
        return ResponseEntity.ok(boardsService.addMember(id, emailId, role, requestingUser));
    }

    @DeleteMapping("/{boardId}/members")
    public ResponseEntity<List<BoardMemberDTO>> removeMember(
            @PathVariable Long boardId,
            @RequestParam String emailId,
            @AuthenticationPrincipal Users reqUser) {

        List<BoardMemberDTO> members = boardsService.removeMember(boardId, emailId, reqUser);
        return ResponseEntity.ok(members);
    }


}