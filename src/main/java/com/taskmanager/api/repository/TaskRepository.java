package com.taskmanager.api.repository;

import com.taskmanager.api.model.Task;
import com.taskmanager.api.model.TaskStatus;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class TaskRepository {

    private final Map<Long, Task> store = new HashMap<>();
    private final AtomicLong idSequence = new AtomicLong(1);

    public Task save(Task task) {
        if (task.getId() == null) {
            task.setId(idSequence.getAndIncrement());
        }
        store.put(task.getId(), task);
        return task;
    }

    public Optional<Task> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public List<Task> findAll() {
        return new ArrayList<>(store.values());
    }

    public List<Task> findByStatus(TaskStatus status) {
        return store.values().stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }

    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    public boolean existsById(Long id) {
        return store.containsKey(id);
    }

    public void clearAll() {
        store.clear();
        idSequence.set(1);
    }
}
