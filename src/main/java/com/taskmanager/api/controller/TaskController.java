package com.taskmanager.api.controller;

import com.taskmanager.api.dto.CreateTaskRequest;
import com.taskmanager.api.dto.UpdateTaskRequest;
import com.taskmanager.api.model.Task;
import com.taskmanager.api.model.TaskStatus;
import com.taskmanager.api.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private static final Logger log = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // US-1: Create a task
    @PostMapping
    public ResponseEntity<Task> createTask(@RequestBody CreateTaskRequest request) {
        log.info("POST /tasks - creating task with title: {}", request.getTitle());
        Task created = taskService.createTask(request.getTitle(), request.getDescription());
        log.info("Task created successfully with id: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // US-2: View all tasks (optional ?status= filter)
    @GetMapping
    public ResponseEntity<List<Task>> getAllTasks(
            @RequestParam(required = false) TaskStatus status) {
        log.info("GET /tasks - retrieving tasks, status filter: {}", status);
        List<Task> tasks = taskService.getAllTasks(status);
        log.info("Returning {} task(s)", tasks.size());
        return ResponseEntity.ok(tasks);
    }

    // US-3: View a single task
    @GetMapping("/{id}")
    public ResponseEntity<Task> getTaskById(@PathVariable Long id) {
        log.info("GET /tasks/{} - retrieving task by id", id);
        return taskService.getTaskById(id)
                .map(task -> {
                    log.info("Task found with id: {}", id);
                    return ResponseEntity.ok(task);
                })
                .orElseGet(() -> {
                    log.error("Task not found with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    // US-4: Mark a task as complete
    @PutMapping("/{id}/complete")
    public ResponseEntity<Task> completeTask(@PathVariable Long id) {
        log.info("PUT /tasks/{}/complete - marking task as completed", id);
        return taskService.completeTask(id)
                .map(task -> {
                    log.info("Task {} marked as COMPLETED", id);
                    return ResponseEntity.ok(task);
                })
                .orElseGet(() -> {
                    log.error("Cannot complete task - not found with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    // US-5: Delete a task
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        log.info("DELETE /tasks/{} - deleting task", id);
        if (taskService.deleteTask(id)) {
            log.info("Task {} deleted successfully", id);
            return ResponseEntity.noContent().build();
        }
        log.error("Cannot delete task - not found with id: {}", id);
        return ResponseEntity.notFound().build();
    }

    // US-6: Update a task
    @PutMapping("/{id}")
    public ResponseEntity<Task> updateTask(@PathVariable Long id,
                                           @RequestBody UpdateTaskRequest request) {
        log.info("PUT /tasks/{} - updating task", id);
        return taskService.updateTask(id, request.getTitle(), request.getDescription())
                .map(task -> {
                    log.info("Task {} updated successfully", id);
                    return ResponseEntity.ok(task);
                })
                .orElseGet(() -> {
                    log.error("Cannot update task - not found with id: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }
}
