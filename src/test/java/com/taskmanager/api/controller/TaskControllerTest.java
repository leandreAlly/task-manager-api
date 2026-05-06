package com.taskmanager.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.api.dto.CreateTaskRequest;
import com.taskmanager.api.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository.clearAll();
    }

    // ── US-1: POST /tasks ────────────────────────────────────────────────────

    @Test
    @DisplayName("US-1: POST /tasks returns 201 and the created task")
    void createTask_returns201AndCreatedTask() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Buy groceries");
        request.setDescription("Milk, eggs, bread");

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Buy groceries"))
                .andExpect(jsonPath("$.description").value("Milk, eggs, bread"))
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty());
    }

    @Test
    @DisplayName("US-1: POST /tasks assigns a unique id to each task")
    void createTask_assignsUniqueIds() throws Exception {
        CreateTaskRequest first = new CreateTaskRequest();
        first.setTitle("Task One");
        first.setDescription("First task");

        CreateTaskRequest second = new CreateTaskRequest();
        second.setTitle("Task Two");
        second.setDescription("Second task");

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(first)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));

        mockMvc.perform(post("/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(second)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2));
    }

    // ── US-2: GET /tasks ─────────────────────────────────────────────────────

    @Test
    @DisplayName("US-2: GET /tasks returns 200 with an empty list when no tasks exist")
    void getAllTasks_returnsEmptyList_whenNoTasksExist() throws Exception {
        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    @DisplayName("US-2: GET /tasks returns 200 with all created tasks")
    void getAllTasks_returnsAllTasks() throws Exception {
        CreateTaskRequest taskA = new CreateTaskRequest();
        taskA.setTitle("Task A");
        taskA.setDescription("Description A");

        CreateTaskRequest taskB = new CreateTaskRequest();
        taskB.setTitle("Task B");
        taskB.setDescription("Description B");

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskA)));

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskB)));

        mockMvc.perform(get("/tasks"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].title", containsInAnyOrder("Task A", "Task B")));
    }

    // ── US-3: GET /tasks/{id} ────────────────────────────────────────────────

    @Test
    @DisplayName("US-3: GET /tasks/{id} returns 200 and the correct task when found")
    void getTaskById_returns200AndTask_whenFound() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Read a book");
        request.setDescription("Finish Clean Code");

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Read a book"))
                .andExpect(jsonPath("$.description").value("Finish Clean Code"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("US-3: GET /tasks/{id} returns 404 when task does not exist")
    void getTaskById_returns404_whenNotFound() throws Exception {
        mockMvc.perform(get("/tasks/999"))
                .andExpect(status().isNotFound());
    }
}
