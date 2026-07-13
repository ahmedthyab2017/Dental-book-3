package com.dantal.task.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateTaskRequest {

    @NotBlank
    private String title;

    private String note;
    private String assignerName;
    private String assigneeStaffId;
    private String assigneeName;
    private Long dueAt;
    private String status;
}
