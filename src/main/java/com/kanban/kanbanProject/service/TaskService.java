package com.kanban.kanbanProject.service;

import com.kanban.kanbanProject.dto.TaskDTO;
import com.kanban.kanbanProject.dto.UserDTO;
import com.kanban.kanbanProject.entity.*;
import com.kanban.kanbanProject.enums.BoardRole;
import com.kanban.kanbanProject.exceptions.KanbanException;
import com.kanban.kanbanProject.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TasksRepo tasksRepo;

    @Autowired
    private ColumnsRepo columnsRepo;

    @Autowired
    private BoardMembersRepo boardMembersRepo;

    @Autowired
    private UsersRepo usersRepo;

    @Autowired
    private BoardsRepo boardsRepo;

    // Create Task
    public TaskDTO createTask(Long columnId, TaskDTO taskDTO, Users user) {

        Columns column = columnsRepo.findById(columnId)
                .orElseThrow(() -> new KanbanException("Column not found", HttpStatus.NOT_FOUND));

        Boards board = column.getBoard();

        BoardMembers member = boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new KanbanException("Not a board member. Access Denied.", HttpStatus.FORBIDDEN));

//        if (member.getRole() == BoardRole.MEMBER) {
//            throw new KanbanException("Only ADMIN or OWNER can create task", HttpStatus.FORBIDDEN);
//        }

        Tasks task = new Tasks();
        task.setTitle(taskDTO.getTitle());
        task.setContent(taskDTO.getContent());
        task.setStatus(taskDTO.getStatus());
        task.setCreatedOn(LocalDateTime.now());
        task.setUpdatedOn(LocalDateTime.now());
        task.setCreatedBy(user);
        task.setColumn(column);

        if (taskDTO.getUserDTO() != null && taskDTO.getUserDTO().getEmailId() != null) {
            Users assignee = usersRepo.findByEmailId(taskDTO.getUserDTO().getEmailId());
            if (assignee == null) {
                throw new KanbanException("User not found", HttpStatus.NOT_FOUND);
            }

            boardMembersRepo.findByBoardAndUser(board, assignee)
                    .orElseThrow(() -> new KanbanException("Assignee is not a board member", HttpStatus.NOT_FOUND));

            task.setUser(assignee);
        }

        Tasks savedTask = tasksRepo.save(task);

        return toDTO(savedTask);
    }

    // Update Task
    public TaskDTO updateTask(Long taskId, TaskDTO taskDTO, Users user) {
        Tasks task = tasksRepo.findById(taskId)
                .orElseThrow(() -> new KanbanException("Task not found", HttpStatus.NOT_FOUND));

        Boards board = task.getColumn().getBoard();

        boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new KanbanException("Not a board member. Access Denied.", HttpStatus.FORBIDDEN));

        if (taskDTO.getTitle() != null) task.setTitle(taskDTO.getTitle());
        if (taskDTO.getContent() != null) task.setContent(taskDTO.getContent());
        if (taskDTO.getStatus() != null) task.setStatus(taskDTO.getStatus());
        if (taskDTO.getUserDTO() != null) {
            Users assignee = usersRepo.findByEmailId(taskDTO.getUserDTO().getEmailId());
            if (assignee == null) {
                throw new KanbanException("User not found", HttpStatus.NOT_FOUND);
            }

            boardMembersRepo.findByBoardAndUser(board, assignee)
                    .orElseThrow(() -> new KanbanException("Assignee is not a board member", HttpStatus.NOT_FOUND));

            task.setUser(assignee);
        }

        task.setUpdatedOn(LocalDateTime.now());

        tasksRepo.save(task);

        return toDTO(task);
    }

    // Assign / reassign a task to a board member
    public TaskDTO assignTask(Long taskId, String emailId, Users user) {

        if (emailId == null || emailId.isBlank()) {
            throw new KanbanException("emailId is required", HttpStatus.BAD_REQUEST);
        }

        Tasks task = tasksRepo.findById(taskId)
                .orElseThrow(() -> new KanbanException("Task not found", HttpStatus.NOT_FOUND));

        Boards board = task.getColumn().getBoard();

        // Check requesting user is a board member with sufficient role
        BoardMembers member = boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new KanbanException("Not a board member. Access Denied.", HttpStatus.FORBIDDEN));

        if (member.getRole() == BoardRole.MEMBER) {
            throw new KanbanException("Only ADMIN or OWNER can assign tasks", HttpStatus.FORBIDDEN);
        }

        Users assignee = usersRepo.findByEmailId(emailId);
        if (assignee == null) {
            throw new KanbanException("User not found", HttpStatus.NOT_FOUND);
        }

        // Check the assignee is actually a member of this board
        boardMembersRepo.findByBoardAndUser(board, assignee)
                .orElseThrow(() -> new KanbanException("User is not a board member", HttpStatus.NOT_FOUND));

        task.setUser(assignee);
        task.setUpdatedOn(LocalDateTime.now());

        Tasks savedTask = tasksRepo.save(task);

        return toDTO(savedTask);
    }

    // Remove assignee from task
    public TaskDTO removeMemberFromTask(Long taskId, String emailId, Users user) {

        Tasks task = tasksRepo.findById(taskId)
                .orElseThrow(() -> new KanbanException("Task not found", HttpStatus.NOT_FOUND));

        Boards board = task.getColumn().getBoard();

        // Check requesting user is a board member with sufficient role
        BoardMembers member = boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new KanbanException("Not a board member. Access Denied.", HttpStatus.FORBIDDEN));

        if (member.getRole() == BoardRole.MEMBER) {
            throw new KanbanException("Only ADMIN or OWNER can remove assignee", HttpStatus.FORBIDDEN);
        }

        // Check task currently has an assignee
        if (task.getUser() == null) {
            throw new KanbanException("Task has no assignee", HttpStatus.BAD_REQUEST);
        }

        // Check the emailId given actually matches the current assignee
        if (!task.getUser().getEmailId().equals(emailId)) {
            throw new KanbanException("User is not assigned to this task", HttpStatus.BAD_REQUEST);
        }

        task.setUser(null);
        task.setUpdatedOn(LocalDateTime.now());

        Tasks savedTask = tasksRepo.save(task);

        return toDTO(savedTask);
    }

    @Transactional
    public TaskDTO moveTask(Long taskId, Long newColumnId, Integer newPosition, Users user) {
        Tasks task = tasksRepo.findById(taskId)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // TODO: your existing authorization check here (board membership etc.)

        Long oldColumnId = task.getColumn().getId();
        Integer oldPosition = task.getPosition();
        boolean sameColumn = oldColumnId.equals(newColumnId);

        Columns newColumn = columnsRepo.findById(newColumnId)
                .orElseThrow(() -> new RuntimeException("Column not found"));

        if (sameColumn) {
            // Reordering within the same column
            if (newPosition < oldPosition) {
                // moved up: shift everything between newPosition and oldPosition-1 down by 1
                tasksRepo.incrementPositionsFrom(oldColumnId, newPosition);
                // the shift above also bumped the task being moved itself if it's in range —
                // safest is to reload, or just set explicitly after
            } else if (newPosition > oldPosition) {
                tasksRepo.decrementPositionsAfter(oldColumnId, oldPosition);
            }
            task.setPosition(newPosition);
        } else {
            // Moving to a different column: close the gap in the old column,
            // make room in the new column, then place it
            tasksRepo.decrementPositionsAfter(oldColumnId, oldPosition);
            tasksRepo.incrementPositionsFrom(newColumnId, newPosition);
            task.setColumn(newColumn);
            task.setPosition(newPosition);
        }

        task.setUpdatedOn(LocalDateTime.now());
        Tasks saved = tasksRepo.save(task);
        return toDTO(saved);
    }

    // Get all tasks for a user
    public List<TaskDTO> getAllTasksForUser(Users user) {
        List<BoardMembers> memberships = boardMembersRepo.findByUser(user);
        List<Boards> boards = memberships.stream()
                .map(BoardMembers::getBoard)
                .toList();

        return tasksRepo.findByColumnBoardIn(boards)
                .stream()
                .map(this::toDTO)

                .toList();
    }

    // Get task by ID
    public TaskDTO getTaskById(Long taskId, Users user) {
        Tasks task = tasksRepo.findById(taskId)
                .orElseThrow(() -> new KanbanException("Task not found", HttpStatus.NOT_FOUND));

        Boards board = task.getColumn().getBoard();

        boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new KanbanException("Not a board member. Access Denied.", HttpStatus.FORBIDDEN));

        return toDTO(task);
    }

    public List<TaskDTO> getTasksByColumnId(Long columnId, Users user) {

        Columns column = columnsRepo.findById(columnId)
                .orElseThrow(() -> new KanbanException("Column not found", HttpStatus.NOT_FOUND));

        Boards board = column.getBoard();

        boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new KanbanException("Not a board member. Access Denied.", HttpStatus.FORBIDDEN));

        List<Tasks> tasks = tasksRepo.findByColumnIdOrderByPositionAsc(columnId);

        return tasks
                .stream()
                .map(this::toDTO)
                .toList();
    }

    // Delete task by ID
    public void deleteTask(Long taskId, Users user) {

        Tasks task = tasksRepo.findById(taskId)
                .orElseThrow(() -> new KanbanException("Task not found", HttpStatus.NOT_FOUND));

        Boards board = task.getColumn().getBoard();

        BoardMembers member = boardMembersRepo.findByBoardAndUser(board, user)
                .orElseThrow(() -> new KanbanException("Not a board member. Access Denied.", HttpStatus.FORBIDDEN));

        if (member.getRole() != BoardRole.ADMIN && member.getRole() != BoardRole.OWNER) {
            throw new KanbanException("Only Owner or Admin can delete tasks", HttpStatus.FORBIDDEN);
        }

        tasksRepo.delete(task);
    }

    // Mapper
    private TaskDTO toDTO(Tasks task) {
        TaskDTO dto = new TaskDTO();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setContent(task.getContent());
        dto.setStatus(task.getStatus());
        dto.setCreatedOn(task.getCreatedOn());
        dto.setUpdatedOn(task.getUpdatedOn());

        if (task.getUser() != null) {
            UserDTO assigneeDTO = new UserDTO();
            assigneeDTO.setFirstName(task.getUser().getFirstName());
            assigneeDTO.setLastName(task.getUser().getLastName());
            assigneeDTO.setEmailId(task.getUser().getEmailId());
            dto.setUserDTO(assigneeDTO);
        }

        return dto;
    }
}
