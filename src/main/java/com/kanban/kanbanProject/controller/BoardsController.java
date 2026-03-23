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

    @PostMapping("/boards")
    public void createBoard(@RequestBody BoardDTO boardDTO) {
        boardsService.createBoard(boardDTO);
    }

    @PutMapping("/boards/{boardId}")
    public void updateBoard(@RequestBody BoardDTO boardDTO,
                            @PathVariable Long boardId) {

        boardsService.updateBoard(boardDTO, boardId);

    }

    @DeleteMapping("/boards/{boardId}")
    public void deleteBoard(@PathVariable Long boardId) {

        boardsService.deleteBoard(boardId);

    }

    @GetMapping("/boards/{userId}")
    public List<Boards> getAllBoards() {
        return boardsService.getAllBoards();
    }
}
