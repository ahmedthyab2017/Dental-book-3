package com.dantal.task.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateTaskRequest {

    private String status;
    private String title;
    private String note;
    private Long dueAt;
}
