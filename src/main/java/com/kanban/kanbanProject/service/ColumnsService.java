package com.kanban.kanbanProject.service;

import com.kanban.kanbanProject.dto.ColumnDTO;
import com.kanban.kanbanProject.dto.TaskDTO;
import com.kanban.kanbanProject.entity.*;
import com.kanban.kanbanProject.enums.BoardRole;
import com.kanban.kanbanProject.exceptions.KanbanException;
import com.kanban.kanbanProject.repository.BoardMembersRepo;
import com.kanban.kanbanProject.repository.BoardsRepo;
import com.kanban.kanbanProject.repository.ColumnsRepo;
import com.kanban.kanbanProject.repository.TasksRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ColumnsService {

    @Autowired
    private ColumnsRepo columnsRepo;

    @Autowired
    private BoardsRepo boardsRepo;

    @Autowired
    private BoardMembersRepo boardMembersRepo;

    @Autowired
    private TasksRepo tasksRepo;

    // Create Column
    public Columns createColumn(Long boardId, ColumnDTO columnDTO, Users user) {

        // Is user eligible to create column in this board
        // So check if user is ADMIN or OWNER of the board
        Boards board = boardsRepo.findById(boardId)
                .orElseThrow(() -> new KanbanException("Board not found.", HttpStatus.NOT_FOUND));

        BoardMembers eligibleBoardUser = boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new KanbanException("Not a board member. Access denied", HttpStatus.FORBIDDEN));

        BoardRole role = eligibleBoardUser.getRole();

        if (!role.equals(BoardRole.OWNER) && !role.equals(BoardRole.ADMIN)) {
            throw new KanbanException("Only Owner or Admin can create columns", HttpStatus.FORBIDDEN);
        }

        Integer maxPosition = columnsRepo.findMaxPositionByBoard(board);

        Columns column = new Columns();
        column.setName(columnDTO.getName());
        column.setPosition(maxPosition == null ? 0 : maxPosition + 1);
        column.setCreatedAt(LocalDateTime.now());
        column.setBoard(board);

        columnsRepo.save(column);

        return column;
    }

    // Update Column
    public void updateColumn(Long columnId, ColumnDTO columnDTO, Users user) {

        Columns column = columnsRepo.findById(columnId)
                .orElseThrow(() -> new KanbanException("Column not found", HttpStatus.NOT_FOUND));

        Boards board = column.getBoard();

        BoardMembers boardMembers = boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new KanbanException("Not a board member. Access Denied.", HttpStatus.FORBIDDEN));

        BoardRole role = boardMembers.getRole();

        if (!role.equals(BoardRole.OWNER) && !role.equals(BoardRole.ADMIN)) {
            throw new KanbanException("Not a member. Cannot edit column for this board", HttpStatus.FORBIDDEN);
        }

        if (columnDTO.getName() != null && !columnDTO.getName().trim().isEmpty()) {
            column.setName(columnDTO.getName());
        }

        if (columnDTO.getPosition() != null) {
            column.setPosition(columnDTO.getPosition());
        }

        columnsRepo.save(column);

    }

    // Get Column
    public ResponseEntity<ColumnDTO> getColumnById(Long columnId, Users user) {

        Columns column = columnsRepo.findById(columnId)
                .orElseThrow(() -> new KanbanException("Column not found", HttpStatus.NOT_FOUND));

        Boards board = column.getBoard();

        BoardMembers boardMembers = boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new KanbanException("Not a board member. Access Denied.", HttpStatus.FORBIDDEN));

        return ResponseEntity.ok(mapEntityToDto(column));

    }

    public ResponseEntity<List<ColumnDTO>> getAllColumns(Long boardId, Users user) {

        Boards board = boardsRepo.findById(boardId)
                .orElseThrow(() -> new KanbanException("Board not found", HttpStatus.NOT_FOUND));

        boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new KanbanException("Not a board member. Access Denied.", HttpStatus.FORBIDDEN));

        List<ColumnDTO> columns = columnsRepo.findByBoardId(board.getId())
                .stream()
                .map(this::mapEntityToDto)
                .toList();

        return ResponseEntity.ok(columns);

    }

    public void deleteColumn(Long columnId, Users user) {
        try {
            Columns column = columnsRepo.findById(columnId)
                    .orElseThrow(() -> new KanbanException("Column not found.", HttpStatus.NOT_FOUND));

            Boards board = column.getBoard();

            boardMembersRepo.findByBoardAndUser(board, user)
                    .orElseThrow(() -> new KanbanException("Not a board member.", HttpStatus.FORBIDDEN));

            tasksRepo.deleteAllByColumn(column);
            columnsRepo.delete(column);

        } catch (DataIntegrityViolationException e) {
            throw new KanbanException(
                    "Delete all tasks in this column first.",
                    HttpStatus.BAD_REQUEST
            );
        }
    }

    // Map Entity to DTO
    private ColumnDTO mapEntityToDto(Columns column) {
        ColumnDTO dto = new ColumnDTO();
        dto.setId(column.getId());
        dto.setName(column.getName());
        dto.setPosition(column.getPosition());
        dto.setCreatedAt(column.getCreatedAt());

        List<Tasks> tasks = tasksRepo.findByColumnIdOrderByPositionAsc(column.getId());
        List<TaskDTO> taskDTOS = new ArrayList<>();
        for(Tasks task : tasks) {
            TaskDTO taskDto = new TaskDTO();
            taskDto.setId(task.getId());
            taskDto.setTitle(task.getTitle());
            taskDto.setContent(task.getContent());
        }

        dto.setTaskDTOS(taskDTOS);
        return dto;

    }
}
