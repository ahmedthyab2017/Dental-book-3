package com.dantal.task.controller;

import com.dantal.security.UserPrincipal;
import com.dantal.task.dto.CreateTaskRequest;
import com.dantal.task.dto.TaskListResponse;
import com.dantal.task.dto.TaskResponse;
import com.dantal.task.dto.UpdateTaskRequest;
import com.dantal.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/v1/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Clinic task management")
@SecurityRequirement(name = "bearerAuth")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @Operation(summary = "List clinic tasks")
    public TaskListResponse list(@AuthenticationPrincipal UserPrincipal principal) {
        return new TaskListResponse(taskService.list(principal));
    }

    @PostMapping
    @Operation(summary = "Create a clinic task")
    public ResponseEntity<TaskResponse> create(@AuthenticationPrincipal UserPrincipal principal,
                                               @Valid @RequestBody CreateTaskRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new TaskResponse(taskService.create(principal, request)));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Update a clinic task")
    public TaskResponse update(@AuthenticationPrincipal UserPrincipal principal,
                               @PathVariable UUID id,
                               @Valid @RequestBody UpdateTaskRequest request) {
        return new TaskResponse(taskService.update(principal, id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a clinic task")
    public ResponseEntity<Void> delete(@AuthenticationPrincipal UserPrincipal principal,
                                       @PathVariable UUID id) {
        taskService.delete(principal, id);
        return ResponseEntity.noContent().build();
    }
}
