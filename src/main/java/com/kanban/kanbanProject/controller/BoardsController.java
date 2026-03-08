package com.kanban.kanbanProject.controller;

import com.kanban.kanbanProject.dto.BoardDTO;
import com.kanban.kanbanProject.entity.Boards;
import com.kanban.kanbanProject.service.BoardsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class BoardsController {

    @Autowired
    private BoardsService boardsService;

    @PostMapping("/boards/{userId}")
    public void createBoard(@RequestBody BoardDTO boardDTO, @PathVariable Long userId) {
        boardsService.createBoard(boardDTO, userId);
    }

    @PutMapping("/boards/{userId}/{boardId}")
    public void updateBoard(@RequestBody BoardDTO boardDTO,
                            @PathVariable Long userId,
                            @PathVariable Long boardId) {

        boardsService.updateBoard(boardDTO, userId, boardId);

    }

    @DeleteMapping("/boards/{userId}/{boardId}")
    public void deleteBoard(@PathVariable Long userId,
                            @PathVariable Long boardId) {

        boardsService.deleteBoard(userId, boardId);

    }

    @GetMapping("/boards/{userId}")
    public List<Boards> getAllBoards(@PathVariable Long userId) {
        return boardsService.getAllBoards(userId);
    }
}
