package com.dantal.task.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TaskListResponse {

    private List<TaskDto> tasks;
}
