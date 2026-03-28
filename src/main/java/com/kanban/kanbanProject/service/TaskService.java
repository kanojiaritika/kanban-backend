package com.kanban.kanbanProject.service;

import com.kanban.kanbanProject.TaskStatus;
import com.kanban.kanbanProject.dto.BoardDTO;
import com.kanban.kanbanProject.dto.TaskDTO;
import com.kanban.kanbanProject.dto.TaskDTOResponse;
import com.kanban.kanbanProject.entity.Boards;
import com.kanban.kanbanProject.entity.Tasks;
import com.kanban.kanbanProject.entity.Users;
import com.kanban.kanbanProject.exceptions.UserNotFoundException;
import com.kanban.kanbanProject.repository.BoardsRepo;
import com.kanban.kanbanProject.repository.TasksRepo;
import com.kanban.kanbanProject.repository.UsersRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    @Autowired
    private UsersRepo userRepo;

    @Autowired
    private BoardsRepo boardRepo;

    @Autowired
    private TasksRepo taskRepo;

    // Create Task
    public ResponseEntity<?> createTask(Long boardId, TaskDTO taskDTO) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Users user = userRepo.findByEmailId(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Boards board = boardRepo.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        Tasks task = new Tasks();
        task.setTitle(taskDTO.getTitle());
        task.setContent(taskDTO.getContent());
        task.setCreatedOn(LocalDateTime.now());
        task.setStatus(TaskStatus.PENDING);
        task.setBoards(board);

        Tasks savedTask = taskRepo.save(task);

        return ResponseEntity.ok(savedTask.getId());
    }

    public ResponseEntity<?> updateTask(Long taskId, Long boardId, TaskDTO taskDTO) {

        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Users user = userRepo.findByEmailId(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Boards existingBoard = Optional.ofNullable(
                boardRepo.findByUsersIdAndId(user.getId(), boardId)
        ).orElseThrow(() -> new RuntimeException("Board not found"));

        Tasks existingTask = taskRepo.findByIdAndBoardsId(taskId, existingBoard.getId())
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (taskDTO.getTitle() != null && !taskDTO.getTitle().isEmpty()) {
            existingTask.setTitle(taskDTO.getTitle());
        }

        if (taskDTO.getContent() != null && !taskDTO.getContent().isEmpty()) {
            existingTask.setContent(taskDTO.getContent());
        }


        existingTask.setUpdatedOn(LocalDateTime.now());

        taskRepo.save(existingTask);

        TaskDTOResponse response = new TaskDTOResponse();
        response.setId(existingTask.getId());
        response.setTitle(existingTask.getTitle());
        response.setContent(existingTask.getContent());
        response.setStatus(existingTask.getStatus());
        response.setCreatedOn(existingTask.getCreatedOn());
        response.setUpdatedOn(existingTask.getUpdatedOn());

        return ResponseEntity.ok(response);
    }

    // Get all tasks
    public ResponseEntity<?> getAllTasks(Long boardId) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        Users user = userRepo.findByEmailId(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Boards existingBoard = Optional.ofNullable(
                boardRepo.findByUsersIdAndId(user.getId(), boardId)
        ).orElseThrow(() -> new RuntimeException("Board not found"));

        BoardDTO boardDTO = new BoardDTO();
        boardDTO.setId(existingBoard.getId());
        boardDTO.setTitle(existingBoard.getTitle());
        boardDTO.setCreatedAt(existingBoard.getCreatedOn());
        List<TaskDTO> taskDTOList = new ArrayList<>();
        for (int i = 0; i < existingBoard.getTasks().size(); i++) {
            TaskDTO taskDTO = new TaskDTO();
            taskDTO.setTitle(existingBoard.getTasks().get(i).getTitle());
            taskDTO.setContent(existingBoard.getTasks().get(i).getContent());
            taskDTOList.add(taskDTO);

        }
        boardDTO.setTasks(taskDTOList);
        return ResponseEntity.ok(boardDTO);
    }
}
