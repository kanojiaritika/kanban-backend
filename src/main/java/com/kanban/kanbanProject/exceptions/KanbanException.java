package com.kanban.kanbanProject.exceptions;

import org.springframework.http.HttpStatus;

public class KanbanException extends RuntimeException {

    private final HttpStatus status;

    public KanbanException(String message, HttpStatus status) {
        super(message);
        this.status = status;

    }

    public HttpStatus getStatus() {
        return status;
    }
}
