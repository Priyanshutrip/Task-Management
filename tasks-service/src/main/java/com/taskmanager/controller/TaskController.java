package com.taskmanager.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskmanager.dto.TaskRequest;
import com.taskmanager.dto.TaskResponse;
import com.taskmanager.dto.UpdateStatusRequest;
import com.taskmanager.entity.Task;
import com.taskmanager.entity.TaskStatus;
import com.taskmanager.repository.TaskRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskRepository taskRepository;

    // ===============================
    // CREATE TASK
    // ===============================
    @PostMapping
    public ResponseEntity<?> createTask(
            @RequestBody TaskRequest request,
            Authentication authentication) {

        String loggedInUser = authentication.getName();

        if (request.getDeadline().isBefore(LocalDate.now())) {
            return ResponseEntity.badRequest()
                    .body("Deadline cannot be in the past");
        }

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .assignedTo(loggedInUser)
                .status(TaskStatus.TODO)
                .deadline(request.getDeadline())
                .build();

        taskRepository.save(task);

        return ResponseEntity.ok("Task created successfully");
    }

    // ===============================
    // GET MY TASKS
    // ===============================
    @GetMapping("/my")
    public ResponseEntity<?> getMyTasks(Authentication authentication) {

        String email = authentication.getName();

        List<Task> tasks = taskRepository.findByAssignedTo(email);

        List<TaskResponse> response = tasks.stream()
                .map(task -> TaskResponse.builder()
                        .id(task.getId())
                        .title(task.getTitle())
                        .description(task.getDescription())
                        .status(task.getStatus())
                        .deadline(task.getDeadline())
                        .build())
                .toList();

        return ResponseEntity.ok(response);
    }


    // ===============================
    // UPDATE STATUS
    // ===============================
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(
            @PathVariable Long id,
            @RequestBody UpdateStatusRequest request,
            Authentication authentication) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        if (!task.getAssignedTo().equals(authentication.getName())) {
            return ResponseEntity.status(403)
                    .body("You are not allowed to update this task");
        }

        try {
            TaskStatus newStatus = TaskStatus.valueOf(request.getStatus());
            task.setStatus(newStatus);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body("Invalid status value");
        }

        taskRepository.save(task);

        return ResponseEntity.ok("Status updated successfully");
    }

    // ===============================
    // ANALYTICS
    // ===============================
    @GetMapping("/analytics")
    public ResponseEntity<?> getAnalytics(Authentication authentication) {

        String email = authentication.getName();

        List<Task> tasks = taskRepository.findByAssignedTo(email);

        long total = tasks.size();
        long todo = tasks.stream().filter(t -> t.getStatus() == TaskStatus.TODO).count();
        long inProgress = tasks.stream().filter(t -> t.getStatus() == TaskStatus.IN_PROGRESS).count();
        long done = tasks.stream().filter(t -> t.getStatus() == TaskStatus.DONE).count();

        return ResponseEntity.ok(
                Map.of(
                        "total", total,
                        "todo", todo,
                        "inProgress", inProgress,
                        "done", done
                )
        );
    }
}
