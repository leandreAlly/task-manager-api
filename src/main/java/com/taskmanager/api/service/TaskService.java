package com.taskmanager.api.service;

import com.taskmanager.api.model.Task;
import com.taskmanager.api.model.TaskStatus;
import com.taskmanager.api.repository.TaskRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Sprint 1
    public Task createTask(String title, String description) {
        Task task = new Task(null, title, description);
        return taskRepository.save(task);
    }

    public List<Task> getAllTasks(TaskStatus status) {
        if (status == null) return taskRepository.findAll();
        return taskRepository.findByStatus(status);
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    // Sprint 2 — US-4
    public Optional<Task> completeTask(Long id) {
        return taskRepository.findById(id).map(task -> {
            task.setStatus(TaskStatus.COMPLETED);
            return taskRepository.save(task);
        });
    }

    // Sprint 2 — US-5
    public boolean deleteTask(Long id) {
        return taskRepository.deleteById(id);
    }

    // Sprint 2 — US-6
    public Optional<Task> updateTask(Long id, String title, String description) {
        return taskRepository.findById(id).map(task -> {
            if (title != null && !title.isBlank()) task.setTitle(title);
            if (description != null && !description.isBlank()) task.setDescription(description);
            return taskRepository.save(task);
        });
    }
}
