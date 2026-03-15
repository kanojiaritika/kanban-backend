package com.kanban.kanbanProject.service;

import com.kanban.kanbanProject.dto.BoardDTO;
import com.kanban.kanbanProject.entity.Boards;
import com.kanban.kanbanProject.entity.Users;
import com.kanban.kanbanProject.exceptions.IncorrectDetails;
import com.kanban.kanbanProject.exceptions.UserNotFoundException;
import com.kanban.kanbanProject.repository.BoardsRepo;
import com.kanban.kanbanProject.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class BoardsService {


    @Autowired
    private BoardsRepo boardsRepo;

    @Autowired
    private UsersRepo usersRepo;

    public void createBoard(BoardDTO boardDTO) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<Users> userExists = usersRepo.findByEmailId(email);

        if (userExists.isEmpty()) {
            throw new UserNotFoundException("User with id " + email + " not found.");
        }

        validateBoard(boardDTO);

        Boards boards = new Boards();
        boards.setTitle(boardDTO.getTitle());
        boards.setCreatedOn(LocalDateTime.now());
        boards.setTasks(boardDTO.getTasks());
        boards.setUsers(userExists.get());

        boardsRepo.save(boards);

    }

    public void updateBoard(BoardDTO boardDTO, Long userId, Long boardId) {

        Boards board = boardsRepo.findByUsersIdAndId(userId, boardId);

        if (board == null) {
            throw new UserNotFoundException("Board id " + boardId + " for user with id " + userId + " not found.");
        }

        board.setTitle(boardDTO.getTitle());
        board.setCreatedOn(LocalDateTime.now());
        board.setTasks(boardDTO.getTasks());

        boardsRepo.save(board);
    }

    public List<Boards> getAllBoards(Long userId) {

        Optional<Users> userExists = usersRepo.findById(userId);

        if (userExists.isEmpty()) {
            throw new UserNotFoundException("User with id " + userId + " not found.");
        }

        return boardsRepo.getAllBoardsByUsersId(userId);

    }

    public void deleteBoard(Long userId, Long boardId) {
        Boards board = boardsRepo.findByUsersIdAndId(userId, boardId);
        if (board == null) {
            throw new UserNotFoundException("Board id " + boardId + " for user with id " + userId + " not found.");
        }

        boardsRepo.delete(board);
    }

    public void validateBoard(BoardDTO boardDTO) {
        if (boardDTO.getTitle() == null || boardDTO.getTitle().trim().isEmpty()) {
            throw new IncorrectDetails("Title cannot be empty.");
        }
    }
}
