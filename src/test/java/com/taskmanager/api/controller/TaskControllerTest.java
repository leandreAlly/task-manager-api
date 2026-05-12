package com.taskmanager.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taskmanager.api.dto.CreateTaskRequest;
import com.taskmanager.api.dto.UpdateTaskRequest;
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

    @Test
    @DisplayName("US-2: GET /tasks?status=PENDING returns only pending tasks")
    void getAllTasks_filterByPending_returnsOnlyPendingTasks() throws Exception {
        CreateTaskRequest taskA = new CreateTaskRequest();
        taskA.setTitle("Pending Task");
        taskA.setDescription("Not done yet");

        CreateTaskRequest taskB = new CreateTaskRequest();
        taskB.setTitle("Done Task");
        taskB.setDescription("Already done");

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskA)));

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskB)));

        mockMvc.perform(put("/tasks/2/complete"));

        mockMvc.perform(get("/tasks").param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Pending Task"))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }

    @Test
    @DisplayName("US-2: GET /tasks?status=COMPLETED returns only completed tasks")
    void getAllTasks_filterByCompleted_returnsOnlyCompletedTasks() throws Exception {
        CreateTaskRequest taskA = new CreateTaskRequest();
        taskA.setTitle("Task Alpha");
        taskA.setDescription("Alpha description");

        CreateTaskRequest taskB = new CreateTaskRequest();
        taskB.setTitle("Task Beta");
        taskB.setDescription("Beta description");

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskA)));

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(taskB)));

        mockMvc.perform(put("/tasks/1/complete"));

        mockMvc.perform(get("/tasks").param("status", "COMPLETED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title").value("Task Alpha"))
                .andExpect(jsonPath("$[0].status").value("COMPLETED"));
    }

    @Test
    @DisplayName("US-2: GET /tasks?status=PENDING returns empty list when all tasks are completed")
    void getAllTasks_filterByPending_returnsEmpty_whenAllCompleted() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Only Task");
        request.setDescription("Will be completed");

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(put("/tasks/1/complete"));

        mockMvc.perform(get("/tasks").param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
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

    // ── US-4: PUT /tasks/{id}/complete ───────────────────────────────────────

    @Test
    @DisplayName("US-4: PUT /tasks/{id}/complete returns 200 and status COMPLETED")
    void completeTask_returns200AndCompletedStatus() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Write report");
        request.setDescription("End of sprint report");

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(put("/tasks/1/complete"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    @DisplayName("US-4: PUT /tasks/{id}/complete returns 404 when task does not exist")
    void completeTask_returns404_whenNotFound() throws Exception {
        mockMvc.perform(put("/tasks/999/complete"))
                .andExpect(status().isNotFound());
    }

    // ── US-5: DELETE /tasks/{id} ─────────────────────────────────────────────

    @Test
    @DisplayName("US-5: DELETE /tasks/{id} returns 204 when task is deleted successfully")
    void deleteTask_returns204_whenDeleted() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Task to delete");
        request.setDescription("Will be removed");

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("US-5: DELETE /tasks/{id} returns 404 when task does not exist")
    void deleteTask_returns404_whenNotFound() throws Exception {
        mockMvc.perform(delete("/tasks/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("US-5: DELETE /tasks/{id} removes the task so it cannot be retrieved afterwards")
    void deleteTask_taskNoLongerRetrievable_afterDeletion() throws Exception {
        CreateTaskRequest request = new CreateTaskRequest();
        request.setTitle("Temporary task");
        request.setDescription("To be deleted");

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)));

        mockMvc.perform(delete("/tasks/1"))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/tasks/1"))
                .andExpect(status().isNotFound());
    }

    // ── US-6: PUT /tasks/{id} ────────────────────────────────────────────────

    @Test
    @DisplayName("US-6: PUT /tasks/{id} returns 200 with updated title and description")
    void updateTask_returns200WithUpdatedFields() throws Exception {
        CreateTaskRequest create = new CreateTaskRequest();
        create.setTitle("Old title");
        create.setDescription("Old description");

        mockMvc.perform(post("/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(create)));

        UpdateTaskRequest update = new UpdateTaskRequest();
        update.setTitle("New title");
        update.setDescription("New description");

        mockMvc.perform(put("/tasks/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("New title"))
                .andExpect(jsonPath("$.description").value("New description"));
    }

    @Test
    @DisplayName("US-6: PUT /tasks/{id} returns 404 when task does not exist")
    void updateTask_returns404_whenNotFound() throws Exception {
        UpdateTaskRequest update = new UpdateTaskRequest();
        update.setTitle("Ghost title");
        update.setDescription("Ghost description");

        mockMvc.perform(put("/tasks/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isNotFound());
    }
}
