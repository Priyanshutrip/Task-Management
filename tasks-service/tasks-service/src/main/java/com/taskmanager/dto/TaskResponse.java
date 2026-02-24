package com.taskmanager.dto;

import java.time.LocalDate;

import com.taskmanager.entity.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private TaskStatus  status;
    private LocalDate deadline;
}
