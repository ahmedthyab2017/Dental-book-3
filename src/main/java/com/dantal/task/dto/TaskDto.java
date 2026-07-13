package com.dantal.task.dto;

import com.dantal.task.entity.Task;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class TaskDto {

    private String id;
    private String title;
    private String note;
    private String assignerName;
    private String assigneeStaffId;
    private String assigneeName;
    private Long dueAt;
    private String status;

    public static TaskDto from(Task task) {
        return TaskDto.builder()
                .id(task.getId().toString())
                .title(task.getTitle())
                .note(task.getNote())
                .assignerName(task.getAssignerName())
                .assigneeStaffId(task.getAssigneeStaffId())
                .assigneeName(task.getAssigneeName())
                .dueAt(task.getDueAt())
                .status(task.getStatus())
                .build();
    }
}
