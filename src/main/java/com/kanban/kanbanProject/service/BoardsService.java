package com.kanban.kanbanProject.service;

import com.kanban.kanbanProject.TaskStatus;
import com.kanban.kanbanProject.dto.BoardDTO;
import com.kanban.kanbanProject.dto.TaskDTO;
import com.kanban.kanbanProject.entity.Boards;
import com.kanban.kanbanProject.entity.Tasks;
import com.kanban.kanbanProject.entity.Users;
import com.kanban.kanbanProject.exceptions.IncorrectDetails;
import com.kanban.kanbanProject.exceptions.UserNotFoundException;
import com.kanban.kanbanProject.repository.BoardsRepo;
import com.kanban.kanbanProject.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    public ResponseEntity<?> createBoard(BoardDTO boardDTO) {

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
        boards.setTasks(mapToTaskEntities(boardDTO.getTasks()));
        boards.setUsers(userExists.get());

        boardsRepo.save(boards);

        return ResponseEntity.ok(boards.getId());

    }

    public void updateBoard(BoardDTO boardDTO, Long boardId) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Users user = usersRepo.findByEmailId(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Boards board = boardsRepo.findByUsersIdAndId(user.getId(), boardId);

        if (board == null) {
            throw new RuntimeException("Board not found for this user");
        }

        if (boardDTO.getTitle() != null && !boardDTO.getTitle().isEmpty()) {
            board.setTitle(boardDTO.getTitle());
        }

        if (boardDTO.getTasks() != null && !boardDTO.getTasks().isEmpty()) {
            board.setTasks(mapToTaskEntities(boardDTO.getTasks()));
        }

        boardsRepo.save(board);
    }

    public List<BoardDTO> getAllBoards() {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Users user = usersRepo.findByEmailId(email).orElseThrow(() -> new UserNotFoundException("User not found"));

        List<Boards> boards = boardsRepo.getAllBoardsByUsersId(user.getId());

        return boards.stream()
                .map(this::mapToBoardDTO)
                .toList();


    }

    public void deleteBoard(Long boardId) {

        String email = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Users user = usersRepo.findByEmailId(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Boards board = boardsRepo.findByUsersIdAndId(user.getId(), boardId);

        if (board == null) {
            throw new UserNotFoundException("Board id " + boardId + " for user with id " + user.getId() + " not found.");
        }

        boardsRepo.delete(board);
    }

    public void validateBoard(BoardDTO boardDTO) {
        if (boardDTO.getTitle() == null || boardDTO.getTitle().trim().isEmpty()) {
            throw new IncorrectDetails("Title cannot be empty.");
        }
    }

    // Map DTO TO Entity
    private List<Tasks> mapToTaskEntities(List<TaskDTO> taskDTOS) {
        return taskDTOS.stream().map(dto -> {
            Tasks task = new Tasks();
            task.setTitle(dto.getTitle());
            task.setContent(dto.getContent());
            return task;
        }).toList();
    }

    // Map Task Entity to Task DTO
    private TaskDTO mapToTaskDTO(Tasks task) {
        TaskDTO dto = new TaskDTO();
        dto.setTitle(task.getTitle());
        dto.setContent(task.getContent());
        return dto;
    }

    // Map Board Entity to Board DTO
    private BoardDTO mapToBoardDTO(Boards board) {
        BoardDTO dto = new BoardDTO();
        dto.setId(board.getId());
        dto.setTitle(board.getTitle());
        dto.setCreatedAt(board.getCreatedOn());

        List<TaskDTO> taskDTOs = board.getTasks()
                .stream()
                .map(this::mapToTaskDTO)
                .toList();

        dto.setTasks(taskDTOs);

        return dto;
    }
}
