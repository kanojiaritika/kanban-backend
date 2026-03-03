package com.kanban.kanbanProject.service;

import com.kanban.kanbanProject.dto.BoardDTO;
import com.kanban.kanbanProject.entity.Boards;
import com.kanban.kanbanProject.exceptions.IncorrectDetails;
import com.kanban.kanbanProject.repository.BoardsRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BoardsService {


    @Autowired
    private BoardsRepo boardsRepo;

    public void createBoard(BoardDTO boardDTO) {

        validateBoard(boardDTO);

        Boards boards = new Boards();
        boards.setTitle(boardDTO.getTitle());
        boards.setCreatedOn(LocalDateTime.now());
        boards.setTasks(boardDTO.getTasks());

        boardsRepo.save(boards);

    }

    public void validateBoard(BoardDTO boardDTO) {
        if (boardDTO.getTitle() == null || boardDTO.getTitle().trim().isEmpty()) {
            throw new IncorrectDetails("Title cannot be empty.");
        }
    }
}
