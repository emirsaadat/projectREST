package com.softserve.itacademy.todolist.controller;
import com.softserve.itacademy.todolist.dto.TaskDto;
import com.softserve.itacademy.todolist.dto.TaskResponse;
import com.softserve.itacademy.todolist.dto.TaskTransformer;
import com.softserve.itacademy.todolist.dto.ToDoResponse;
import com.softserve.itacademy.todolist.model.State;
import com.softserve.itacademy.todolist.model.Task;
import com.softserve.itacademy.todolist.model.ToDo;
import com.softserve.itacademy.todolist.service.StateService;
import com.softserve.itacademy.todolist.service.TaskService;
import com.softserve.itacademy.todolist.service.ToDoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users/{u_id}/todos/{t_id}/tasks")
public class TaskController {

    private final TaskService taskService;
    private final ToDoService toDoService;
    private final StateService stateService;

    public TaskController(TaskService taskService, ToDoService toDoService, StateService stateService) {
        this.taskService = taskService;
        this.toDoService = toDoService;
        this.stateService = stateService;
    }

    @GetMapping
    public List<TaskResponse> getAllTasks(@PathVariable("u_id") long userId, @PathVariable("t_id") long todoId) {
        return toDoService.readById(todoId).getTasks().stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public TaskResponse getTaskById(@PathVariable("u_id") long userId, @PathVariable("t_id") long todoId, @PathVariable("id") long taskId) {
        Task task = taskService.readById(taskId);
        if (task.getTodo().getId() != todoId) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }
        return new TaskResponse(task);
    }

    @PostMapping("/create")
    public List<TaskResponse> createTask(@PathVariable("u_id") long userId, @PathVariable("t_id") long todoId, @RequestBody TaskDto taskDto) {
        ToDo todo = toDoService.readById(todoId);
        State state = stateService.readById(taskDto.getStateId());
        taskService.create(TaskTransformer.convertToEntity(taskDto, todo, state));
        return taskService.getByTodoId(todoId).stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}/update")
    public TaskResponse updateTask(@PathVariable("u_id") long userId, @PathVariable("t_id") long todoId, @PathVariable("id") long taskId, @RequestBody TaskDto taskDto) {
        Task existingTask = taskService.readById(taskId);
        if (existingTask.getTodo().getId() != todoId) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }
        ToDo todo = toDoService.readById(todoId);
        State state = stateService.readById(taskDto.getStateId());
        Task updatedTask = TaskTransformer.convertToEntity(taskDto, todo, state);
        updatedTask.setId(taskId);
        return new TaskResponse(taskService.update(updatedTask));
    }

    @DeleteMapping("/{id}/delete")
    public List<TaskResponse> deleteTask(@PathVariable("u_id") long userId, @PathVariable("t_id") long todoId, @PathVariable("id") long taskId) {
        Task task = taskService.readById(taskId);
        if (task.getTodo().getId() != todoId) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
        }
        taskService.delete(taskId);
        return taskService.getByTodoId(todoId).stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }
}
