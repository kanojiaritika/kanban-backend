package com.kanban.kanbanProject.service;

import com.kanban.kanbanProject.dto.LabelDTO;
import com.kanban.kanbanProject.entity.*;
import com.kanban.kanbanProject.enums.BoardRole;
import com.kanban.kanbanProject.repository.BoardMembersRepo;
import com.kanban.kanbanProject.repository.BoardsRepo;
import com.kanban.kanbanProject.repository.LabelRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LabelService {

    @Autowired
    private BoardsRepo boardsRepo;

    @Autowired
    private BoardMembersRepo boardMembersRepo;

    @Autowired
    private LabelRepo labelRepo;

    // CRUD

    // Create label
    public void createLabel(Long boardId, LabelDTO dto, Users user) {
        Boards board = boardsRepo.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        getAdminOrOwner(board, user);

        Labels label = new Labels();
        label.setName(dto.getName());
        label.setColor(dto.getColor());
        label.setBoard(board);

        labelRepo.save(label);
    }

    // Get all labels for a board
    public List<LabelDTO> getLabelsByBoard(Long boardId, Users user) {
        Boards board = boardsRepo.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new RuntimeException("Not a board member. Access denied"));

        return labelRepo.findByBoardId(boardId)
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // Update label
    public void updateLabel(Long boardId, Long labelId, LabelDTO dto, Users user) {
        Boards board = boardsRepo.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        getAdminOrOwner(board, user);

        Labels label = labelRepo.findById(labelId)
                .orElseThrow(() -> new RuntimeException("Label not found"));

        if (dto.getName() != null) label.setName(dto.getName());
        if (dto.getColor() != null) label.setColor(dto.getColor());

        labelRepo.save(label);
    }

    // Delete label
    public void deleteLabel(Long boardId, Long labelId, Users user) {
        Boards board = boardsRepo.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        getAdminOrOwner(board, user);

        Labels label = labelRepo.findById(labelId)
                .orElseThrow(() -> new RuntimeException("Label not found"));

        labelRepo.delete(label);
    }

    private LabelDTO toDTO(Labels label) {
        LabelDTO dto = new LabelDTO();
        dto.setId(label.getId());
        dto.setName(label.getName());
        dto.setColor(label.getColor());
        return dto;
    }

    // Helper (Check if Admin or Owner)
    private BoardMembers getAdminOrOwner(Boards board, Users user) {
        BoardMembers member = boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new RuntimeException("Not a board member. Access denied"));

        BoardRole role = member.getRole();
        if (!role.equals(BoardRole.ADMIN) && !role.equals(BoardRole.OWNER)) {
            throw new RuntimeException("Only Admin or Owner can manage labels");
        }

        return member;
    }
}