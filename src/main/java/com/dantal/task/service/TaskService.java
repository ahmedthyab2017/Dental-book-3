package com.dantal.task.service;

import com.dantal.common.exception.BusinessException;
import com.dantal.common.exception.ResourceNotFoundException;
import com.dantal.common.security.ClinicContext;
import com.dantal.security.UserPrincipal;
import com.dantal.task.dto.CreateTaskRequest;
import com.dantal.task.dto.TaskDto;
import com.dantal.task.dto.UpdateTaskRequest;
import com.dantal.task.entity.Task;
import com.dantal.task.repository.TaskRepository;
import com.dantal.user.entity.User;
import com.dantal.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<TaskDto> list(UserPrincipal principal) {
        UUID clinicId = ClinicContext.requireClinicId(principal);
        return taskRepository.findByClinicIdOrderByCreatedAtDesc(clinicId).stream()
                .map(TaskDto::from)
                .toList();
    }

    @Transactional
    public TaskDto create(UserPrincipal principal, CreateTaskRequest request) {
        UUID clinicId = ClinicContext.requireClinicId(principal);
        User user = userRepository.getReferenceById(principal.getId());

        Task task = new Task();
        task.setClinicId(clinicId);
        task.setTitle(request.getTitle().trim());
        task.setNote(request.getNote());
        task.setAssignerName(request.getAssignerName());
        task.setAssigneeStaffId(request.getAssigneeStaffId());
        task.setAssigneeName(request.getAssigneeName());
        task.setDueAt(request.getDueAt());
        task.setStatus(normalizeStatus(request.getStatus()));
        task.setCreatedBy(user);

        return TaskDto.from(taskRepository.save(task));
    }

    @Transactional
    public TaskDto update(UserPrincipal principal, UUID taskId, UpdateTaskRequest request) {
        UUID clinicId = ClinicContext.requireClinicId(principal);
        Task task = taskRepository.findByIdAndClinicId(taskId, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));

        if (request.getStatus() != null) {
            task.setStatus(normalizeStatus(request.getStatus()));
        }
        if (request.getTitle() != null && !request.getTitle().isBlank()) {
            task.setTitle(request.getTitle().trim());
        }
        if (request.getNote() != null) {
            task.setNote(request.getNote());
        }
        if (request.getDueAt() != null) {
            task.setDueAt(request.getDueAt());
        }

        return TaskDto.from(taskRepository.save(task));
    }

    @Transactional
    public void delete(UserPrincipal principal, UUID taskId) {
        UUID clinicId = ClinicContext.requireClinicId(principal);
        Task task = taskRepository.findByIdAndClinicId(taskId, clinicId)
                .orElseThrow(() -> new ResourceNotFoundException("Task", taskId));
        taskRepository.delete(task);
    }

    private String normalizeStatus(String status) {
        if (status == null || status.isBlank()) {
            return "open";
        }
        String normalized = status.trim().toLowerCase();
        if (!normalized.equals("open") && !normalized.equals("done")) {
            throw new BusinessException("Invalid task status", HttpStatus.BAD_REQUEST, "INVALID_STATUS");
        }
        return normalized;
    }
}
