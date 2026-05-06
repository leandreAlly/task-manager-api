package com.taskmanager.api.controller;

import com.taskmanager.api.service.TaskService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    // Sprint 1 endpoints will be implemented in Step 2:
    //   POST   /tasks
    //   GET    /tasks
    //   GET    /tasks/{id}

    // Sprint 2 endpoints will be implemented in Step 8:
    //   PUT    /tasks/{id}/complete
    //   DELETE /tasks/{id}
    //   PUT    /tasks/{id}
}
