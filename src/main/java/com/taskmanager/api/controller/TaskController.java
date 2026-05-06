package com.taskmanager.api.controller;

import com.taskmanager.api.dto.CreateTaskRequest;
import com.taskmanager.api.dto.UpdateTaskRequest;
import com.taskmanager.api.model.Task;
import com.taskmanager.api.service.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // US-1: Create a task
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody CreateTaskRequest request) {
        Task created = taskService.createTask(request.getTitle(), request.getDescription());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // US-2: View all tasks
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks() {
        return ResponseEntity.ok(taskService.getAllTasks());
    }

    // US-3: View a single task
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // US-4: Mark a task as complete
    @PutMapping("/{id}/complete")
    public ResponseEntity<Task> completeTask(@PathVariable Long id) {
        return taskService.completeTask(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // US-5: Delete a task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (taskService.deleteTask(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    // US-6: Update a task
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id,
                                           @RequestBody UpdateTaskRequest request) {
        return taskService.updateTask(id, request.getTitle(), request.getDescription())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
