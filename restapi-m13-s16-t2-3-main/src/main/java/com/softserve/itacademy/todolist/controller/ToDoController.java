package com.softserve.itacademy.todolist.controller;

import com.softserve.itacademy.todolist.dto.TaskResponse;
import com.softserve.itacademy.todolist.dto.ToDoResponse;
import com.softserve.itacademy.todolist.dto.UserResponse;
import com.softserve.itacademy.todolist.model.ToDo;
import com.softserve.itacademy.todolist.model.User;
import com.softserve.itacademy.todolist.service.ToDoService;
import com.softserve.itacademy.todolist.service.UserService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class ToDoController {

    final ToDoService toDoService;
    final UserService userService;

    public ToDoController(ToDoService toDoService, UserService userService) {
        this.toDoService = toDoService;
        this.userService = userService;
    }

    @GetMapping("/{id}/todos")
    List<ToDoResponse> getAllByUserId(@PathVariable long id) {
        return toDoService.getByUserId(id).stream()
                .map(ToDoResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/{userId}/todos/{todoId}/collaborators")
    List<UserResponse> getAllByCollaboratorId(@PathVariable long userId, @PathVariable long todoId) {
        List<User> collaborators = new ArrayList<>();

        collaborators.add(userService.readById(userId));

        collaborators.addAll(toDoService.getByUserId(userId).stream()
                .filter(toDo -> toDo.getId().equals(todoId))
                .flatMap(toDo -> toDo.getCollaborators().stream())
                .toList());

        return collaborators.stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}/todos/{todoId}/update")
    public ToDoResponse updateToDo(@PathVariable long id, @PathVariable long todoId, @RequestBody ToDo toDo) {
        toDo.setCreatedAt(toDoService.readById(todoId).getCreatedAt());
        toDo.setOwner(userService.readById(id));
        toDo.setTasks(toDoService.readById(todoId).getTasks());
        toDo.setCollaborators(toDoService.readById(todoId).getCollaborators());
        return new ToDoResponse(toDoService.update(toDo));
    }

    @DeleteMapping("/{userId}/todos/{todoId}/delete")
    public List<ToDoResponse> deleteToDo(@PathVariable long userId, @PathVariable long todoId) {
        toDoService.delete(todoId);
        return toDoService.getByUserId(userId).stream()
                .map(ToDoResponse::new)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{userId}/todos/create")
    public List<ToDoResponse> createToDo(@PathVariable long userId, @RequestBody ToDo toDo) {
        toDo.setCreatedAt(LocalDateTime.now());
        toDo.setOwner(userService.readById(userId));
        return toDoService.getByUserId(userId).stream()
                .map(ToDoResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/todos/{todoId}/read")
    public ToDoResponse readToDo(@PathVariable long todoId) {
        return new ToDoResponse(toDoService.readById(todoId));
    }

    @PatchMapping("/todos/{todoId}/collaborators/{userId}/add")
    List<UserResponse> addCollaborator(@PathVariable long userId, @PathVariable long todoId) {
        toDoService.readById(todoId).getCollaborators().add(userService.readById(userId));
        return toDoService.readById(todoId).getCollaborators().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/todos/{todoId}/collaborators/{userId}/delete")
    List<UserResponse> removeCollaborator(@PathVariable long userId, @PathVariable long todoId) {
        toDoService.readById(todoId).getCollaborators().remove(userService.readById(userId));
        return toDoService.readById(todoId).getCollaborators().stream()
                .map(UserResponse::new)
                .collect(Collectors.toList());
    }

    @GetMapping("/todos/{todoId}/tasks")
    List<TaskResponse> todoTasks(@PathVariable long todoId) {
        return toDoService.readById(todoId).getTasks().stream()
                .map(TaskResponse::new)
                .collect(Collectors.toList());
    }
}
