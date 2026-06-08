package com.kanban.kanbanProject.service;

import com.kanban.kanbanProject.dto.BoardDTO;
import com.kanban.kanbanProject.entity.BoardMembers;
import com.kanban.kanbanProject.entity.Boards;
import com.kanban.kanbanProject.entity.Users;
import com.kanban.kanbanProject.enums.BoardRole;
import com.kanban.kanbanProject.repository.BoardMembersRepo;
import com.kanban.kanbanProject.repository.BoardsRepo;
import com.kanban.kanbanProject.repository.UsersRepo;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.stream.Collectors;

@Service
public class BoardsService {

    @Autowired
    private BoardsRepo boardsRepo;

    @Autowired
    private BoardMembersRepo boardMembersRepo;

    @Autowired
    private UsersRepo usersRepo;

    private static final Logger log = LoggerFactory.getLogger(BoardsService.class);

    // Create a board
    public Boards createBoard(BoardDTO boardDTO, Users currentUser) {

        log.info("Creating board for user: {}");

        // Create Boards Object
        Boards board = new Boards();
        board.setTitle(boardDTO.getTitle());
        board.setCreatedOn(LocalDateTime.now());
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

    // Get boards of current user
    public List<Boards> getMyBoards(Users currentUser) {
        return boardMembersRepo.findByUser(currentUser)
                .stream()
                .map(BoardMembers::getBoard)
                .collect(Collectors.toList());
    }

    // Get Board by ID
    public Boards getBoard(Long boardId, Users requestingUser) {
        Boards board = boardsRepo.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        // Check membership
        boardMembersRepo.findByBoardAndUser(board, requestingUser)
                .orElseThrow(() -> new RuntimeException("Access denied"));

        return board;
    }

    // Update title (only OWNER or ADMIN)
    public Boards updateBoard(Long boardId, String newTitle, Users requestingUser) {
        Boards board = boardsRepo.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        BoardMembers membership = boardMembersRepo
                .findByBoardAndUser(board, requestingUser)
                .orElseThrow(() -> new RuntimeException("Access denied"));

        if (membership.getRole() == BoardRole.MEMBER) {
            throw new RuntimeException("Only OWNER or ADMIN can update board");
        }

        board.setTitle(newTitle);
        return boardsRepo.save(board);
    }

    // Delete board (only OWNER)
    public void deleteBoard(Long boardId, Users requestingUser) {
        Boards board = boardsRepo.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        BoardMembers membership = boardMembersRepo
                .findByBoardAndUser(board, requestingUser)
                .orElseThrow(() -> new RuntimeException("Access denied"));

        if (membership.getRole() != BoardRole.OWNER) {
            throw new RuntimeException("Only OWNER can delete a board");
        }

        boardsRepo.delete(board);
    }

    // Add a member in board
    public BoardMembers addMember(Long boardId, Long newMemberId,
                                  BoardRole role, Users requestingUser) {
        Boards board = boardsRepo.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found."));

        BoardMembers requestingMembership = boardMembersRepo
                .findByBoardAndUser(board, requestingUser)
                .orElseThrow(() -> new RuntimeException("User does not belong to this board."));

        if (requestingMembership.getRole() == BoardRole.MEMBER) {
            throw new RuntimeException("Only OWNER or ADMIN can add members.");
        }

        Users newMember = usersRepo.findById(newMemberId)
                .orElseThrow(() -> new RuntimeException("User not found."));

        // Prevent duplicate membership
        if (boardMembersRepo.findByBoardAndUser(board, newMember).isPresent()) {
            throw new RuntimeException("User is already a member of this board.");
        }

        // Prevent a non-OWNER from assigning OWNER role
        if (role == BoardRole.OWNER && requestingMembership.getRole() != BoardRole.OWNER) {
            throw new RuntimeException("Only OWNER can assign the OWNER role.");
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


}
