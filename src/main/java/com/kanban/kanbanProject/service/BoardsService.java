package com.kanban.kanbanProject.service;

import com.kanban.kanbanProject.dto.BoardDTO;
import com.kanban.kanbanProject.dto.BoardMemberDTO;
import com.kanban.kanbanProject.entity.*;
import com.kanban.kanbanProject.enums.BoardRole;
import com.kanban.kanbanProject.exceptions.KanbanException;
import com.kanban.kanbanProject.repository.*;
import jakarta.transaction.Transactional;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BoardsService {

    @Autowired
    private BoardsRepo boardsRepo;

    @Autowired
    private BoardMembersRepo boardMembersRepo;

    @Autowired
    private ColumnsRepo columnsRepo;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private TasksRepo tasksRepo;

    private static final Logger log = LoggerFactory.getLogger(BoardsService.class);

    // Create a board
    @Transactional
    public Boards createBoard(BoardDTO boardDTO, Users currentUser) {

        if (boardDTO.getTitle().isEmpty()) {
            throw new KanbanException("Please Enter Title.", HttpStatus.BAD_REQUEST);
        }

        if (boardDTO.getDescription().isEmpty()) {
            throw new KanbanException("Please enter description", HttpStatus.BAD_REQUEST);
        }

        // Create Boards Object
        Boards board = new Boards();
        board.setTitle(boardDTO.getTitle());
        board.setDescription(boardDTO.getDescription());
        board.setCreatedOn(LocalDateTime.now());
        board.setUpdatedOn(LocalDateTime.now());
        board.setCreatedby(currentUser);
        boardsRepo.save(board);

        // Create Board Members object (Which board belongs to which user and its role)
        BoardMembers boardMember = new BoardMembers();
        boardMember.setBoard(board);
        boardMember.setUser(currentUser);
        boardMember.setRole(BoardRole.OWNER); // The one who crates Board is the OWNER.
        boardMember.setIsActive(true);
        boardMember.setJoinedAt(LocalDateTime.now());
        boardMember.setUpdatedAt(LocalDateTime.now());
        boardMembersRepo.save(boardMember);

        return board;

    }

    // Update title (only OWNER or ADMIN)
    public Boards updateBoard(Long boardId, BoardDTO boardDTO, Users requestingUser) {
        Boards board = boardsRepo.findById(boardId)
                .orElseThrow(() -> new KanbanException("Board not found", HttpStatus.NOT_FOUND));

        BoardMembers membership = boardMembersRepo
                .findByBoardAndUser(board, requestingUser)
                .orElseThrow(() -> new KanbanException("Access denied", HttpStatus.FORBIDDEN));

        if (membership.getRole() == BoardRole.MEMBER) {
            throw new KanbanException("Only OWNER or ADMIN can update board", HttpStatus.FORBIDDEN);
        }

        if (!boardDTO.getTitle().isEmpty()) {
            board.setTitle(boardDTO.getTitle());
        }

        if (!boardDTO.getDescription().isEmpty()) {
            board.setDescription(boardDTO.getDescription());
        }

        board.setUpdatedOn(LocalDateTime.now());
        return boardsRepo.save(board);
    }

    // Add favorite board for a user
    public BoardDTO makeBoardFavorite(Long boardId, Users currentUser) {
//        BoardMembers boardMem = boardMembersRepo.findB
        Boards board = boardsRepo.findById(boardId)
                .orElseThrow(() -> new KanbanException("Board not found", HttpStatus.NOT_FOUND));

        BoardMembers boardMember = boardMembersRepo.findByBoardAndUser(board, currentUser)
                .orElseThrow(() -> new KanbanException("Not found", HttpStatus.NOT_FOUND));

        // Toggle favorite
        boardMember.setIsFavorite(!Boolean.TRUE.equals(boardMember.getIsFavorite()));

        boardMembersRepo.save(boardMember);

        // Create response DTO
        BoardDTO dto = new BoardDTO();
        dto.setId(board.getId());
        dto.setTitle(board.getTitle());
        dto.setDescription(board.getDescription());
        dto.setIsFavorite(boardMember.getIsFavorite());

        return dto;
    }

    // Get favorite boards
    public List<BoardDTO> getFavoriteBoards(Users currentUser) {

        List<BoardMembers> favoriteMembers =
                boardMembersRepo.findByUserAndIsFavorite(currentUser, true);

        return favoriteMembers.stream()
                .map(bm -> {
                    Boards board = bm.getBoard();

                    BoardDTO dto = new BoardDTO();
                    dto.setId(board.getId());
                    dto.setTitle(board.getTitle());
                    dto.setDescription(board.getDescription());
                    dto.setCreatedOn(board.getCreatedOn());
                    dto.setUpdatedOn(board.getUpdatedOn());
                    dto.setIsFavorite(true);

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Archive board
    public void archiveBoard(Long boardId, Users currentUser) {

        Boards board = boardsRepo.findById(boardId)
                .orElseThrow(() -> new KanbanException("Board not found", HttpStatus.NOT_FOUND));

        BoardMembers boardMember = boardMembersRepo.findByBoardAndUser(board, currentUser)
                .orElseThrow(() -> new KanbanException("Board not found", HttpStatus.NOT_FOUND));

        if (boardMember.getRole() != BoardRole.OWNER) {
            throw new KanbanException("Only Owner can archive board", HttpStatus.FORBIDDEN);
        }

        board.setIsArchived(true);
        boardsRepo.save(board);

    }

    // Unarchive Board
    public void unarchiveBoard(Long boardId, Users currentUser) {

        Boards board = boardsRepo.findById(boardId)
                .orElseThrow(() -> new KanbanException("Board not found",HttpStatus.NOT_FOUND));

        BoardMembers boardMember = boardMembersRepo
                .findByBoardAndUser(board, currentUser)
                .orElseThrow(() ->
                        new KanbanException(
                                "Board not found", HttpStatus.NOT_FOUND));

        if (boardMember.getRole() != BoardRole.OWNER) {
            throw new KanbanException("Cannot unarchive board", HttpStatus.FORBIDDEN);
        }

        board.setIsArchived(false);
        boardsRepo.save(board);
    }

    // Get archived boards
    public List<BoardDTO> getArchivedBoards(Users currentUser) {

        List<Boards> boards = boardMembersRepo.findByUser(currentUser)
                .stream()
                .map(BoardMembers::getBoard)
                .filter(board -> Boolean.TRUE.equals(board.getIsArchived()))
                .collect(Collectors.toList());

        return boards.stream()
                .map(board -> {

                    BoardDTO dto = new BoardDTO();

                    dto.setId(board.getId());
                    dto.setTitle(board.getTitle());
                    dto.setDescription(board.getDescription());
                    dto.setCreatedOn(board.getCreatedOn());
                    dto.setUpdatedOn(board.getUpdatedOn());
                    dto.setIsArchived(true);

                    dto.setIsFavorite(
                            boardMembersRepo.findByBoardAndUser(board, currentUser)
                                    .map(BoardMembers::getIsFavorite)
                                    .orElse(false)
                    );

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Get boards of current user
    public List<BoardDTO> getMyBoards(Users currentUser) {

        List<Boards> boards = boardMembersRepo.findByUser(currentUser)
                .stream()
                .map(BoardMembers::getBoard)
                .filter(board -> !Boolean.TRUE.equals(board.getIsArchived()))
                .collect(Collectors.toList());

        return boards.stream()
                .map(board -> {

                    List<BoardMemberDTO> members = boardMembersRepo.findByBoard(board)
                            .stream()
                            .filter(bm -> Boolean.TRUE.equals(bm.getIsActive()))
                            .map(bm -> new BoardMemberDTO(
                                    bm.getUser().getId(),
                                    bm.getUser().getFirstName(),
                                    bm.getUser().getLastName(),
                                    bm.getUser().getEmailId(),
                                    bm.getRole()
                            ))
                            .collect(Collectors.toList());

                    return new BoardDTO(
                            board.getId(),
                            board.getTitle(),
                            board.getDescription(),
                            board.getCreatedOn(),
                            board.getUpdatedOn(),
                            members,
                            boardMembersRepo.findByBoardAndUser(board, currentUser)
                                    .get().getRole(),
                            boardMembersRepo.findByBoardAndUser(board, currentUser)
                                    .map(BoardMembers::getIsFavorite)
                                    .orElse(false),
                            board.getIsArchived()
                    );
                })
                .collect(Collectors.toList());
    }

    // Get Board by ID
    public BoardDTO getBoard(Long boardId, Users requestingUser) {
        Boards board = boardsRepo.findById(boardId)
                .orElseThrow(() -> new KanbanException("Board not found", HttpStatus.NOT_FOUND));

        // Check membership
        BoardMembers member = boardMembersRepo.findByBoardAndUser(board, requestingUser)
                .orElseThrow(() -> new KanbanException("Access denied", HttpStatus.FORBIDDEN));

        // User opened the board
        member.setLastOpenedAt(LocalDateTime.now());
        boardMembersRepo.save(member);

        BoardDTO dto = new BoardDTO();
        dto.setId(board.getId());
        dto.setTitle(board.getTitle());
        dto.setDescription(board.getDescription());
        dto.setCreatedOn(board.getCreatedOn());
        dto.setUpdatedOn(board.getUpdatedOn());
        dto.setIsFavorite(boardMembersRepo.findByBoardAndUser(board, requestingUser)
                .get().getIsFavorite());
        dto.setUserRole(boardMembersRepo.findByBoardAndUser(board, requestingUser)
                .get().getRole());
        dto.setIsArchived(board.getIsArchived());

        return dto;
    }

    // Get Shared Boards
    public List<BoardDTO> getSharedBoards(Users currentUser) {
        List<Boards> sharedBoards = boardsRepo.findSharedBoards(currentUser);

        List<BoardDTO> ans = new ArrayList<>();
        for (Boards board : sharedBoards) {
            BoardDTO dto = new BoardDTO();
            dto.setId(board.getId());
            dto.setTitle(board.getTitle());
            dto.setDescription(board.getDescription());
            dto.setCreatedOn(board.getCreatedOn());
            dto.setUpdatedOn(board.getUpdatedOn());
            dto.setIsFavorite(boardMembersRepo.findByBoardAndUser(board, currentUser)
                    .get().getIsFavorite());
            dto.setIsArchived(board.getIsArchived());
            ans.add(dto);
        }

        return ans;

    }

    public List<BoardDTO> getRecentlyOpened(Users currentUser) {

        return boardMembersRepo.findRecentlyOpened(currentUser)
                .stream()
                .limit(3)
                .map(bm -> {
                    Boards board = bm.getBoard();

                    BoardDTO dto = new BoardDTO();
                    dto.setId(board.getId());
                    dto.setTitle(board.getTitle());
                    dto.setDescription(board.getDescription());
                    dto.setIsFavorite(bm.getIsFavorite());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    // Delete board (only OWNER)
    @Transactional
    public ResponseEntity<String> deleteBoard(Long boardId, Users requestingUser) {
        Boards board = boardsRepo.findById(boardId)
                .orElseThrow(() -> new KanbanException("Board not found", HttpStatus.NOT_FOUND));

        BoardMembers membership = boardMembersRepo
                .findByBoardAndUser(board, requestingUser)
                .orElseThrow(() -> new KanbanException("Access denied", HttpStatus.FORBIDDEN));

        if (membership.getRole() != BoardRole.OWNER) {
            throw new KanbanException("Only OWNER can delete a board", HttpStatus.FORBIDDEN);
        }

        boardMembersRepo.deleteAllByBoard(board);

        // Find columns by board
        List<Columns> columns = columnsRepo.findByBoardId(boardId);

        // Delete all tasks by column
        for (Columns column : columns) {
            tasksRepo.deleteAllByColumn(column);
        }

        columnsRepo.deleteAllByBoard(board);

        boardsRepo.delete(board);

        return ResponseEntity.ok("Board deleted");
    }

    // Add a member in board
    public BoardMembers addMember(Long boardId, String emailId,
                                  BoardRole role, Users requestingUser) {
        Boards board = boardsRepo.findById(boardId)
                .orElseThrow(() -> new KanbanException("Board not found.", HttpStatus.NOT_FOUND));

        BoardMembers requestingMembership = boardMembersRepo
                .findByBoardAndUser(board, requestingUser)
                .orElseThrow(() -> new KanbanException("User does not belong to this board.", HttpStatus.NOT_FOUND));

        if (requestingMembership.getRole() == BoardRole.MEMBER) {
            throw new KanbanException("Only OWNER or ADMIN can add members.", HttpStatus.FORBIDDEN);
        }

        Users newMember = usersRepo.findByEmailId(emailId);
        if (newMember == null) {
            throw new KanbanException("User not found", HttpStatus.NOT_FOUND);
        }

        // Prevent duplicate membership
        if (boardMembersRepo.findByBoardAndUser(board, newMember).isPresent()) {
            throw new KanbanException("User is already a member of this board.", HttpStatus.FORBIDDEN);
        }

        // Prevent a non-OWNER from assigning OWNER role
        if (role == BoardRole.OWNER && requestingMembership.getRole() != BoardRole.OWNER) {
            throw new KanbanException("Only OWNER can assign the OWNER role.", HttpStatus.FORBIDDEN);
        }

        BoardMembers membership = new BoardMembers();
        membership.setBoard(board);
        membership.setUser(newMember);
        membership.setRole(role);
        membership.setIsActive(true);
        membership.setJoinedAt(LocalDateTime.now());
        membership.setUpdatedAt(LocalDateTime.now());

        return boardMembersRepo.save(membership);
    }

    public List<BoardMemberDTO> removeMember(Long boardId, String emailId, Users reqUser) {

        Boards board = boardsRepo.findById(boardId)
                .orElseThrow(() -> new KanbanException("Board not found.", HttpStatus.NOT_FOUND));

        BoardMembers requestingMembership = boardMembersRepo
                .findByBoardAndUser(board, reqUser)
                .orElseThrow(() -> new KanbanException("User does not belong to this board.", HttpStatus.NOT_FOUND));

        Users userToRemove = usersRepo.findByEmailId(emailId);
        if (userToRemove == null) {
            throw new KanbanException("User not found", HttpStatus.NOT_FOUND);
        }

        BoardMembers membershipToRemove = boardMembersRepo
                .findByBoardAndUser(board, userToRemove)
                .orElseThrow(() -> new KanbanException("User is not a member of this board.", HttpStatus.NOT_FOUND));

        if (requestingMembership.getRole() == BoardRole.ADMIN
                && membershipToRemove.getRole() != BoardRole.MEMBER) {
            throw new KanbanException("Only the OWNER can remove an ADMIN or OWNER.", HttpStatus.FORBIDDEN);
        }

        boardMembersRepo.delete(membershipToRemove);

        List<BoardMembers> boardMembers = boardMembersRepo.findByBoardId(boardId);
        List<BoardMemberDTO> ans = new ArrayList<>();
        for (BoardMembers boardMem : boardMembers) {
            BoardMemberDTO dto = new BoardMemberDTO();
            dto.setUserId(boardMem.getUser().getId());
            dto.setFirstName(boardMem.getUser().getFirstName());
            dto.setLastName(boardMem.getUser().getLastName());
            dto.setEmailId(boardMem.getUser().getEmailId());
            dto.setRole(boardMem.getRole());
            ans.add(dto);
        }

        return ans;

    }



}
